package com.sun.training.employeemanagement.controller;

import com.sun.training.employeemanagement.dto.EmployeeRequestDto;
import com.sun.training.employeemanagement.dto.EmployeeResponseDto;
import com.sun.training.employeemanagement.entity.Department;
import com.sun.training.employeemanagement.exception.DuplicateResourceException;
import com.sun.training.employeemanagement.repository.DepartmentRepository;
import com.sun.training.employeemanagement.service.EmployeeService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Spring MVC Web Controller handling Employee views and form interactions via Thymeleaf (Module 6).
 */
@Controller
@RequestMapping("/employees")
public class EmployeeWebController {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmployeeWebController.class);

    private final EmployeeService employeeService;
    private final DepartmentRepository departmentRepository;

    /**
     * Constructor injection for services and repositories.
     *
     * @param employeeService employee service bean
     * @param departmentRepository department repository bean
     */
    public EmployeeWebController(EmployeeService employeeService, DepartmentRepository departmentRepository) {
        this.employeeService = employeeService;
        this.departmentRepository = departmentRepository;
    }

    /**
     * Displays the employee list page with optional filtering by department and keyword.
     *
     * @param department optional department filter
     * @param keyword optional keyword filter
     * @param model Spring MVC model
     * @return employee list view template
     */
    @GetMapping({"", "/list"})
    public String listEmployees(
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String keyword,
            Model model) {
        LOGGER.info("GET /employees/list (department='{}', keyword='{}')", department, keyword);
        List<EmployeeResponseDto> employees = employeeService.getAllEmployees(department, keyword);
        List<String> departments = departmentRepository.findAll().stream()
                .map(Department::getName)
                .toList();

        model.addAttribute("employees", employees);
        model.addAttribute("departments", departments);
        model.addAttribute("selectedDepartment", department);
        model.addAttribute("keyword", keyword);
        return "employee/list";
    }

    /**
     * Displays the form for creating a new employee.
     *
     * @param model Spring MVC model
     * @return employee add form view template
     */
    @GetMapping("/add")
    public String showAddForm(Model model) {
        LOGGER.info("GET /employees/add form requested");
        if (!model.containsAttribute("employee")) {
            model.addAttribute("employee", new EmployeeRequestDto());
        }
        populateDepartments(model);
        return "employee/add";
    }

    /**
     * Handles submission of the employee creation form with Bean Validation and Data Binding.
     *
     * @param requestDto validated form backing object
     * @param bindingResult validation results
     * @param model Spring MVC model
     * @param redirectAttributes redirect flash attributes
     * @return redirect to list on success, or return to add form with validation errors
     */
    @PostMapping("/add")
    public String processAddEmployee(
            @Valid @ModelAttribute("employee") EmployeeRequestDto requestDto,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        LOGGER.info("POST /employees/add submitted for email: {}", requestDto.getEmail());

        if (bindingResult.hasErrors()) {
            LOGGER.warn("Form validation errors: {}", bindingResult.getErrorCount());
            populateDepartments(model);
            return "employee/add";
        }

        try {
            EmployeeResponseDto created = employeeService.createEmployee(requestDto);
            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    String.format("Thêm nhân viên '%s' (%s) thành công!", created.getFullName(), created.getEmployeeCode()));
            return "redirect:/employees/list";
        } catch (DuplicateResourceException ex) {
            LOGGER.warn("Duplicate email when submitting form: {}", ex.getMessage());
            bindingResult.rejectValue("email", "duplicate", ex.getMessage());
            populateDepartments(model);
            return "employee/add";
        }
    }

    /**
     * Displays the form for editing an existing employee.
     *
     * @param employeeCode unique employee code
     * @param model Spring MVC model
     * @return employee edit form view template
     */
    @GetMapping("/edit/{employeeCode}")
    public String showEditForm(@PathVariable String employeeCode, Model model) {
        LOGGER.info("GET /employees/edit/{} form requested", employeeCode);
        EmployeeResponseDto existing = employeeService.getEmployeeByCode(employeeCode);
        if (!model.containsAttribute("employee")) {
            EmployeeRequestDto formDto = new EmployeeRequestDto();
            formDto.setFullName(existing.getFullName());
            formDto.setEmail(existing.getEmail());
            formDto.setDepartment(existing.getDepartment());
            formDto.setSalary(existing.getSalary());
            model.addAttribute("employee", formDto);
        }
        model.addAttribute("employeeCode", employeeCode);
        populateDepartments(model);
        return "employee/edit";
    }

    /**
     * Handles submission of the employee edit form with validation.
     *
     * @param employeeCode unique employee code
     * @param requestDto validated employee payload
     * @param bindingResult validation results
     * @param model Spring MVC model
     * @param redirectAttributes redirect flash attributes
     * @return redirect to list on success, or return to edit form with validation errors
     */
    @PostMapping("/edit/{employeeCode}")
    public String processEditEmployee(
            @PathVariable String employeeCode,
            @Valid @ModelAttribute("employee") EmployeeRequestDto requestDto,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        LOGGER.info("POST /employees/edit/{} submitted for email: {}", employeeCode, requestDto.getEmail());

        if (bindingResult.hasErrors()) {
            LOGGER.warn("Edit validation errors: {}", bindingResult.getErrorCount());
            model.addAttribute("employeeCode", employeeCode);
            populateDepartments(model);
            return "employee/edit";
        }

        try {
            EmployeeResponseDto updated = employeeService.updateEmployee(employeeCode, requestDto);
            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    String.format("Cập nhật nhân viên '%s' (%s) thành công!", updated.getFullName(), updated.getEmployeeCode()));
            return "redirect:/employees/list";
        } catch (DuplicateResourceException ex) {
            LOGGER.warn("Duplicate email when editing employee: {}", ex.getMessage());
            bindingResult.rejectValue("email", "duplicate", ex.getMessage());
            model.addAttribute("employeeCode", employeeCode);
            populateDepartments(model);
            return "employee/edit";
        }
    }

    /**
     * Displays a dedicated employee search page with filtering by name and department.
     *
     * @param name optional name keyword
     * @param department optional department name
     * @param model Spring MVC model
     * @return employee search view template
     */
    @GetMapping("/search")
    public String searchEmployees(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String department,
            Model model) {
        LOGGER.info("GET /employees/search (name='{}', department='{}')", name, department);
        List<EmployeeResponseDto> results;
        if ((name != null && !name.isBlank()) || (department != null && !department.isBlank())) {
            results = employeeService.getAllEmployees(department, name);
        } else {
            results = List.of();
        }

        List<String> departments = departmentRepository.findAll().stream()
                .map(Department::getName)
                .toList();

        model.addAttribute("results", results);
        model.addAttribute("departments", departments);
        model.addAttribute("searchName", name);
        model.addAttribute("searchDepartment", department);
        model.addAttribute("hasSearched", (name != null || department != null));
        return "employee/search";
    }

    /**
     * Deletes an employee via web action and redirects back to the employee list with flash message.
     *
     * @param employeeCode unique employee code
     * @param redirectAttributes redirect flash attributes
     * @return redirect to employee list
     */
    @GetMapping("/delete/{employeeCode}")
    public String deleteEmployee(
            @PathVariable String employeeCode,
            RedirectAttributes redirectAttributes) {
        LOGGER.info("GET /employees/delete/{} requested from web UI", employeeCode);
        employeeService.deleteEmployee(employeeCode);
        redirectAttributes.addFlashAttribute(
                "successMessage", String.format("Đã xóa nhân viên mã '%s' thành công!", employeeCode));
        return "redirect:/employees/list";
    }

    /**
     * Web endpoint for asynchronous email existence check in web forms.
     *
     * @param email email to verify
     * @param excludeCode optional employee code to exclude
     * @return map with exists flag
     */
    @GetMapping("/check-email")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> checkEmail(
            @RequestParam String email,
            @RequestParam(required = false) String excludeCode) {
        LOGGER.info("GET /employees/check-email (email='{}', excludeCode='{}')", email, excludeCode);
        boolean exists = employeeService.isEmailExists(email, excludeCode);
        return ResponseEntity.ok(java.util.Map.of("exists", exists));
    }

    private void populateDepartments(Model model) {
        List<String> departments = departmentRepository.findAll().stream()
                .map(Department::getName)
                .toList();
        model.addAttribute("departments", departments);
    }
}

