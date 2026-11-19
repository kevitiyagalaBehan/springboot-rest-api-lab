package lk.student.api.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lk.student.api.demo.model.Student;

/**
 * Request body for creating or updating a student.
 * Using a DTO instead of the JPA entity prevents clients from setting
 * fields they should not control (e.g. the database id) - mass assignment.
 */
public record StudentRequest(
		@NotBlank @Size(max = 100) String name,
		@NotBlank @Email @Size(max = 150) String email,
		@NotBlank @Size(max = 50) String course) {

	public Student toEntity() {
		return new Student(name, email, course);
	}
}
