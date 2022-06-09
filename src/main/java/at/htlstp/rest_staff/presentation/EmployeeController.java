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
import java.util.Locale;

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
        partial_name = partial_name.toLowerCase(Locale.ROOT);

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
            return employeeRepository.getAllHoursWorkedByEmployee(id);
        } catch (AopInvocationException e) {//Wenn der Employee keine Tasks hat, wird 0 returned
            return 0;
        }
    }

    @GetMapping("{id}/tasks")
    public List<Task> allTasksBetweenForEmployee(@PathVariable String id, @RequestParam(name = "from", required = false) String from, @RequestParam(name = "to", required = false) String to) {
        //Wenn beide Felder null sind, werden einfach alle Tasks ausgegeben
        if (from == null && to == null) return employeeRepository.findTasksForEmployee(id);
        //Wenn nur ein Feld null ist, wird eine BAD-Request geschmissen
        if (from == null || to == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        return employeeRepository.findTasksBetween(id, LocalDate.parse(from), LocalDate.parse(to));
    }

}
