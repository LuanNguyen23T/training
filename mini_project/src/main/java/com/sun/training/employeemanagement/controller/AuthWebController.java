package com.sun.training.employeemanagement.controller;

import com.sun.training.employeemanagement.dto.RegisterRequestDto;
import com.sun.training.employeemanagement.entity.Role;
import com.sun.training.employeemanagement.exception.DuplicateResourceException;
import com.sun.training.employeemanagement.service.AuthService;
import jakarta.validation.Valid;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Web Controller providing HTML view navigation for user login and registration (Module 9).
 */
@Controller
public class AuthWebController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthWebController.class);

    private final AuthService authService;

    /**
     * Constructor injection for AuthService.
     *
     * @param authService auth service bean
     */
    public AuthWebController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Redirects root URL to the employee list or login.
     *
     * @return redirect view name
     */
    @GetMapping("/")
    public String index() {
        return "redirect:/employees/list";
    }

    /**
     * Renders the login page with optional error or logout status messages.
     *
     * @param error optional error flag from Spring Security
     * @param logout optional logout flag
     * @param registered optional flag when redirected from successful registration
     * @param model UI model
     * @return login view name
     */
    @GetMapping("/login")
    public String showLoginPage(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            @RequestParam(value = "registered", required = false) String registered,
            Model model) {
        LOGGER.info("Rendering web login page (error={}, logout={}, registered={})", error != null, logout != null, registered != null);

        if (error != null) {
            model.addAttribute("errorMessage", "Tên đăng nhập hoặc mật khẩu không chính xác!");
        }
        if (logout != null) {
            model.addAttribute("successMessage", "Bạn đã đăng xuất thành công khỏi hệ thống.");
        }
        if (registered != null) {
            model.addAttribute("successMessage", "Đăng ký tài khoản thành công! Bạn có thể đăng nhập ngay bây giờ.");
        }

        return "auth/login";
    }

    /**
     * Renders the user registration form with username and password fields.
     *
     * @param model UI model
     * @return register view name
     */
    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        LOGGER.info("Rendering web register page");
        if (!model.containsAttribute("registerDto")) {
            RegisterRequestDto dto = new RegisterRequestDto();
            dto.setRole(Role.ROLE_USER);
            model.addAttribute("registerDto", dto);
        }
        return "auth/register";
    }

    /**
     * Web endpoint for asynchronous username existence check in registration form.
     *
     * @param username username to verify
     * @return map with exists flag
     */
    @GetMapping({"/check-username", "/api/v1/auth/check-username"})
    @ResponseBody
    public ResponseEntity<Map<String, Object>> checkUsername(@RequestParam String username) {
        LOGGER.info("GET /check-username (username='{}')", username);
        boolean exists = authService.isUsernameExists(username);
        return ResponseEntity.ok(Map.of("exists", exists));
    }

    /**
     * Renders a custom 403 Access Denied view when a user attempts an unauthorized action.
     *
     * @return 403 error view name
     */
    @GetMapping("/access-denied")
    public String showAccessDeniedPage() {
        LOGGER.warn("Rendering web 403 access denied page");
        return "error/403";
    }

    /**
     * Processes registration form submission and creates user in database with ROLE_USER.
     *
     * @param registerDto form payload
     * @param bindingResult validation results
     * @param model UI model
     * @return redirect to login page or back to register on error
     */
    @PostMapping("/register")
    public String processRegistration(
            @Valid @ModelAttribute("registerDto") RegisterRequestDto registerDto,
            BindingResult bindingResult,
            Model model) {
        LOGGER.info("Processing web registration for username: {}", registerDto.getUsername());

        // Strictly enforce ROLE_USER for web registrations
        registerDto.setRole(Role.ROLE_USER);

        if (bindingResult.hasErrors()) {
            return "auth/register";
        }

        try {
            authService.register(registerDto);
            LOGGER.info("User {} successfully registered and saved into database", registerDto.getUsername());
            return "redirect:/login?registered=true";
        } catch (DuplicateResourceException ex) {
            LOGGER.warn("Registration rejected: {}", ex.getMessage());
            model.addAttribute("errorMessage", ex.getMessage());
            return "auth/register";
        }
    }
}
