package ru.ravilov.PasrseXMLApp.service;

import org.springframework.jdbc.core.RowMapper;
import ru.ravilov.PasrseXMLApp.model.Employee;

/**
 * Этот класс обрабатывает отдельно каждую запись, полученную из БД,
 * и возвращает уже готовый объект - модель данных
 */

import java.sql.ResultSet;
import java.sql.SQLException;

public class CustomerRowMapper implements RowMapper<Employee> {

    @Override
    public Employee mapRow(ResultSet resultSet, int i) throws SQLException {
        Employee employee = new Employee();
        employee.setDepCode(resultSet.getString("depCode"));
        employee.setDepJob(resultSet.getString("depJob"));
        employee.setDescription(resultSet.getString("description"));
        return employee;
    }
}
