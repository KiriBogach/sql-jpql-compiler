package model.jpa;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the class database table.
 * 
 */
@Entity
@NamedQuery(name="Class.findAll", query="SELECT c FROM Class c")
public class Class implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private String classID;

	private String day;

	private String instructor;

	private String time;

	private String title;

	//bi-directional many-to-many association to Student
	@ManyToMany
	@JoinTable(
		name="classstudentrelation"
		, joinColumns={
			@JoinColumn(name="ClassID")
			}
		, inverseJoinColumns={
			@JoinColumn(name="StudentID")
			}
		)
	private List<Student> students;

	public Class() {
	}

	public String getClassID() {
		return this.classID;
	}

	public void setClassID(String classID) {
		this.classID = classID;
	}

	public String getDay() {
		return this.day;
	}

	public void setDay(String day) {
		this.day = day;
	}

	public String getInstructor() {
		return this.instructor;
	}

	public void setInstructor(String instructor) {
		this.instructor = instructor;
	}

	public String getTime() {
		return this.time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<Student> getStudents() {
		return this.students;
	}

	public void setStudents(List<Student> students) {
		this.students = students;
	}

}