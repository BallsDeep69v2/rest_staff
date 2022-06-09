package at.htlstp.rest_staff.presentation;

import at.htlstp.rest_staff.domain.Employee;
import at.htlstp.rest_staff.domain.Task;
import at.htlstp.rest_staff.domain.exceptions.NoSuchEmployeeException;
import at.htlstp.rest_staff.persistence.EmployeeRepository;
import org.springframework.aop.AopInvocationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("api/employees")
public record EmployeeController(EmployeeRepository employeeRepository) {

    @GetMapping("{id}")
    public Employee one(@PathVariable String id) {
        return employeeRepository
                .findById(id)
                .orElseThrow(NoSuchEmployeeException::new);
    }


    @GetMapping
    public List<Employee> byNameContaining(@RequestParam(required = false, name = "name") String partial_name) {
        if (partial_name == null || partial_name.isBlank()) {
            return employeeRepository.findAll();
        }
        return employeeRepository.searchByName(partial_name);
    }

    @PostMapping
    public ResponseEntity<Employee> save(@RequestBody @Valid Employee employee) {
        var saved = employeeRepository.save(employee);

        var uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .build(saved.getId());
        return ResponseEntity
                .created(uri)
                .body(saved);
    }

    @GetMapping("{id}/hoursWorked")
    public int allHoursWorkedByEmployee(@PathVariable String id) {
        try {
            return employeeRepository.getAllHoursByEmployee(id);
        } catch (AopInvocationException e) {
            return 0;
        }
    }

    @GetMapping("{id}/tasks")
    public List<Task> allTasksBetweenForEmployee(@PathVariable String id, @RequestParam(name = "from", required = false) String from, @RequestParam(name = "to", required = false) String to) {
        if (from == null && to == null) return employeeRepository.findTasksForEmployee(id);
        if (from == null || to == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        return employeeRepository.findTasksBetween(id, LocalDate.parse(from), LocalDate.parse(to));
    }

}
