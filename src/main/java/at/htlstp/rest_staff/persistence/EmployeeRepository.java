package at.htlstp.rest_staff.persistence;

import at.htlstp.rest_staff.domain.Employee;
import at.htlstp.rest_staff.domain.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;


public interface EmployeeRepository extends JpaRepository<Employee, String> {

    @Query(
            """
                    select task
                    from Task task
                    where task.employee.id = :employeeId
                    and task.finished between :from and :to
                    """
    )
    List<Task> findTasksBetween(String employeeId, LocalDate from, LocalDate to);

    @Query(
            """
                    select employee
                    from Employee employee
                    where lower(employee.firstName) like %:name%
                    or lower(employee.lastName) like %:name%
                    """
    )
    List<Employee> searchByName(String name);


    @Query(
            """
                    select sum(task.hoursWorked)
                    from Task task
                    where task.employee.id = :employeeId
                 """
    )
    int getAllHoursWorkedByEmployee(String employeeId);

    @Query(
            """
                    select task
                    from Task task
                    where task.employee.id = :employeeId
                    """
    )
    List<Task> findTasksForEmployee(String employeeId);
}
