package ru.ravilov.PasrseXMLApp.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.relational.core.mapping.Column;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
/**
 * Модель данных
 */
@Data
@NoArgsConstructor
@XmlRootElement
public class Employee {
    private Integer id;
    private String depCode;
    private String depJob;
    private String description;

    public Employee(String depCode, String depJob, String description) {
        this.depCode = depCode;
        this.depJob = depJob;
        this.description = description;
    }
}
