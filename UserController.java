package com.example.spring_cap;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import com.stripe.exception.StripeException;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserRepository userRepository;
    private final FavoriteRepository favoriteRepository;
    private final PackageRepository packageRepository;
    private final PaymentService paymentService;
    private final PaymentOrderRepository paymentOrderRepository;
    private final ContactMessageRepository contactMessageRepository;

    public UserController(UserRepository userRepository, FavoriteRepository favoriteRepository,
                          PackageRepository packageRepository, PaymentService paymentService,
                          PaymentOrderRepository paymentOrderRepository,
                          ContactMessageRepository contactMessageRepository) {
        this.userRepository = userRepository;
        this.favoriteRepository = favoriteRepository;
        this.packageRepository = packageRepository;
        this.paymentService = paymentService;
        this.paymentOrderRepository = paymentOrderRepository;
        this.contactMessageRepository = contactMessageRepository;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email already exists"));
        }
        UserType userType = userRepository.count() == 0 ? UserType.ADMIN : UserType.USER;
        user.setType(userType);
        userRepository.save(user);
        return ResponseEntity.ok(Map.of("message", "User registered successfully"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        Optional<User> existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser.isPresent() && existingUser.get().getPassword().equals(user.getPassword())) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Login successful");
            response.put("id", existingUser.get().getId());
            response.put("name", existingUser.get().getName());
            response.put("email", existingUser.get().getEmail());
            response.put("type", existingUser.get().getType().toString());
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(Map.of("message", "Invalid credentials"));
    }

    @GetMapping("/isAdmin")
    public ResponseEntity<?> isAdmin(@RequestParam String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent() && user.get().getType() == UserType.ADMIN) {
            return ResponseEntity.ok(Map.of("isAdmin", true));
        }
        return ResponseEntity.ok(Map.of("isAdmin", false));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }

    @PostMapping("/favorites")
    public ResponseEntity<?> toggleFavorite(@RequestBody Map<String, Long> request) {
        Long userId = request.get("userId");
        Long packageId = request.get("packageId");

        logger.info("Toggle favorite request: userId={}, packageId={}", userId, packageId);

        Optional<User> user = userRepository.findById(userId);
        Optional<Package> pkg = packageRepository.findById(packageId);

        if (user.isEmpty() || pkg.isEmpty()) {
            logger.error("User or Package not found: userId={}, packageId={}", userId, packageId);
            return ResponseEntity.badRequest().body(Map.of("message", "User or Package not found"));
        }

        if (favoriteRepository.existsByUserIdAndPackageObjId(userId, packageId)) {
            favoriteRepository.deleteByUserIdAndPackageObjId(userId, packageId);
            logger.info("Favorite removed: userId={}, packageId={}", userId, packageId);
        } else {
            Favorite favorite = new Favorite();
            favorite.setUser(user.get());
            favorite.setPackageObj(pkg.get());
            favoriteRepository.save(favorite);
            logger.info("Favorite added: userId={}, packageId={}", userId, packageId);
        }

        List<Package> favorites = favoriteRepository.findByUserId(userId)
                .stream()
                .map(Favorite::getPackageObj)
                .collect(Collectors.toList());

        return ResponseEntity.ok(favorites);
    }

    @DeleteMapping("/favorites")
    public ResponseEntity<?> removeFavorite(@RequestBody Map<String, Long> request) {
        Long userId = request.get("userId");
        Long packageId = request.get("packageId");

        logger.info("Remove favorite request: userId={}, packageId={}", userId, packageId);

        if (userId == null || packageId == null) {
            logger.error("Invalid request: userId or packageId is null");
            return ResponseEntity.badRequest().body(Map.of("message", "User ID or Package ID is missing"));
        }

        if (!favoriteRepository.existsByUserIdAndPackageObjId(userId, packageId)) {
            logger.warn("Favorite not found: userId={}, packageId={}", userId, packageId);
            return ResponseEntity.badRequest().body(Map.of("message", "Favorite not found"));
        }

        try {
            favoriteRepository.deleteByUserIdAndPackageObjId(userId, packageId);
            logger.info("Favorite successfully removed: userId={}, packageId={}", userId, packageId);
        } catch (Exception e) {
            logger.error("Error removing favorite: userId={}, packageId={}, error={}", userId, packageId, e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of("message", "Failed to remove favorite", "error", e.getMessage()));
        }

        List<Package> favorites = favoriteRepository.findByUserId(userId)
                .stream()
                .map(Favorite::getPackageObj)
                .collect(Collectors.toList());

        return ResponseEntity.ok(favorites);
    }

    @GetMapping("/favorites")
    public ResponseEntity<List<Package>> getFavorites(@RequestParam Long userId) {
        List<Package> favorites = favoriteRepository.findByUserId(userId)
                .stream()
                .map(Favorite::getPackageObj)
                .collect(Collectors.toList());
        return ResponseEntity.ok(favorites);
    }

    @PostMapping("/book")
    public ResponseEntity<?> bookPackage(@RequestBody Map<String, Long> request) {
        Long userId = request.get("userId");
        Long packageId = request.get("packageId");

        Optional<User> user = userRepository.findById(userId);
        Optional<Package> pkg = packageRepository.findById(packageId);

        if (user.isEmpty() || pkg.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "User or Package not found"));
        }

        PaymentOrder order = paymentService.createOrder(user.get(), pkg.get());
        try {
            String clientSecret = paymentService.createPaymentIntent(user.get(), order.getAmount(), packageId);
            return ResponseEntity.ok(Map.of("clientSecret", clientSecret, "orderId", order.getId()));
        } catch (StripeException e) {
            logger.error("Stripe error creating payment intent: {}", e.getMessage());
            return ResponseEntity.status(500).body(Map.of("message", "Failed to create payment intent", "error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Unexpected error creating payment intent: {}", e.getMessage());
            return ResponseEntity.status(500).body(Map.of("message", "Failed to create payment intent", "error", e.getMessage()));
        }
    }

    @PostMapping("/confirm-payment")
    public ResponseEntity<?> confirmPayment(@RequestBody Map<String, String> request) {
        Long orderId = Long.valueOf(request.get("orderId"));
        String paymentId = request.get("paymentId");

        PaymentOrder order = paymentOrderRepository.findById(orderId).orElse(null);
        if (order == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Order not found"));
        }

        boolean success = paymentService.proceedPaymentOrder(order, paymentId);
        return ResponseEntity.ok(Map.of("success", success));
    }

    @GetMapping("/bookings/count")
    public ResponseEntity<?> getBookingCount(@RequestParam Long packageId) {
        long count = paymentOrderRepository.countByPackageObjIdAndStatus(packageId, PaymentOrderStatus.COMPLETED);
        return ResponseEntity.ok(Map.of("count", count));
    }

    @GetMapping("/bookings")
    public ResponseEntity<List<Package>> getBookings(@RequestParam Long userId) {
        List<Package> bookings = paymentOrderRepository.findByUserIdAndStatus(userId, PaymentOrderStatus.COMPLETED)
                .stream()
                .map(PaymentOrder::getPackageObj)
                .collect(Collectors.toList());
        return ResponseEntity.ok(bookings);
    }

    @PostMapping("/contact")
    public ResponseEntity<?> submitContactForm(@RequestBody ContactMessage message) {
        try {
            contactMessageRepository.save(message);
            return ResponseEntity.ok(Map.of("message", "Submitted successfully"));
        } catch (Exception e) {
            logger.error("Error saving contact message: {}", e.getMessage());
            return ResponseEntity.status(500).body(Map.of("message", "Failed to submit form"));
        }
    }

    @GetMapping("/contact/messages")
    public ResponseEntity<List<ContactMessage>> getContactMessages(@RequestParam String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent() && user.get().getType() == UserType.ADMIN) {
            return ResponseEntity.ok(contactMessageRepository.findAll());
        }
        return ResponseEntity.status(403).body(null);
    }


}