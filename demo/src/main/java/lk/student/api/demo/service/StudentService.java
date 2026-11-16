package lk.student.api.demo.service;

import java.util.List;

import lk.student.api.demo.exception.StudentNotFoundException;
import lk.student.api.demo.model.Student;
import lk.student.api.demo.repository.StudentRepository;
import org.springframework.stereotype.Service;

@Service
public class StudentService {

	private final StudentRepository repository;

	public StudentService(StudentRepository repository) {
		this.repository = repository;
	}

	public List<Student> findAll() {
		return repository.findAll();
	}

	public Student findById(Long id) {
		return repository.findById(id).orElseThrow(() -> new StudentNotFoundException(id));
	}

	public Student create(Student student) {
		student.setId(null);
		return repository.save(student);
	}

	public Student update(Long id, Student update) {
		Student existing = findById(id);
		existing.setName(update.getName());
		existing.setEmail(update.getEmail());
		existing.setCourse(update.getCourse());
		return repository.save(existing);
	}

	public void delete(Long id) {
		repository.delete(findById(id));
	}
}
