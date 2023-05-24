package com.luv2code.springmvc.service;

import com.luv2code.springmvc.models.*;
import com.luv2code.springmvc.repo.HistoryGradeDao;
import com.luv2code.springmvc.repo.MathGradeDao;
import com.luv2code.springmvc.repo.ScienceGradeDao;
import com.luv2code.springmvc.repo.StudentDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class StudentAndGradeService {

    @Autowired
    private StudentDao studentDao;

    @Autowired
    private MathGradeDao mathGradeDao;

    @Autowired
    private HistoryGradeDao historyGradeDao;

    @Autowired
    private ScienceGradeDao scienceGradeDao;

    @Autowired
    @Qualifier("mathGrades")
    private MathGrade mathGrade;

    @Autowired
    @Qualifier("scienceGrades")
    private ScienceGrade scienceGrade;

    @Autowired
    @Qualifier("historyGrades")
    private HistoryGrade historyGrade;

    @Autowired
    private StudentGrades studentGrades;

    public void createStudent(String firstname, String lastname, String emailAddress){
        CollegeStudent student = new CollegeStudent(firstname, lastname, emailAddress);
        student.setId(0);
        studentDao.save(student);
    }

    public boolean isStudentExist(int id) {
        Optional<CollegeStudent> student = studentDao.findById(id);
        if(student.isPresent()){
            return true;
        }
        return false;
    }

    public void deleteStudent(int id) {
        if(isStudentExist(id)){
            studentDao.deleteById(id);
            mathGradeDao.deleteByStudentId(id);
            scienceGradeDao.deleteByStudentId(id);
            historyGradeDao.deleteByStudentId(id);
        }
    }

    public Iterable<CollegeStudent> getGradeBook() {
        Iterable<CollegeStudent> collegeStudents = studentDao.findAll();
        return collegeStudents;
    }

    public boolean createGrade(double grade, int studentId, String gradeType) {
        if(!isStudentExist(studentId)){
            return false;
        }
        if(grade >= 0 && grade <= 100){
            if(gradeType.equals("math")){
                mathGrade.setId(0);
                mathGrade.setStudentId(studentId);
                mathGrade.setGrade(grade);
                mathGradeDao.save(mathGrade);
                return true;
            }
            if(gradeType.equals("history")){
                historyGrade.setId(0);
                historyGrade.setStudentId(studentId);
                historyGrade.setGrade(grade);
                historyGradeDao.save(historyGrade);
                return true;
            }
            if(gradeType.equals("science")){
                scienceGrade.setId(0);
                scienceGrade.setStudentId(studentId);
                scienceGrade.setGrade(grade);
                scienceGradeDao.save(scienceGrade);
                return true;
            }
        }
        return false;
    }

    public int deleteGrade(int id, String gradeType){
        if(gradeType.equals("math")){
            Optional<MathGrade> grade = mathGradeDao.findById(id);
            if(grade.isPresent()){
                mathGradeDao.deleteById(id);
                return grade.get().getStudentId();
            }
            return -1;
        }
        if(gradeType.equals("science")){
            Optional<ScienceGrade> grade = scienceGradeDao.findById(id);
            if(grade.isPresent()){
                scienceGradeDao.deleteById(id);
                return grade.get().getStudentId();
            }
            return -1;
        }
        if(gradeType.equals("history")){
            Optional<HistoryGrade> grade = historyGradeDao.findById(id);
            if(grade.isPresent()){
                historyGradeDao.deleteById(id);
                return grade.get().getStudentId();
            }
            return -1;
        }
        return -1;
    }

    public GradebookCollegeStudent getStudentInfo(int id) {
        Optional<CollegeStudent> student = studentDao.findById(id);
        if(!student.isPresent()){
            return null;
        }
        Iterable<MathGrade> mathGrades = mathGradeDao.findGradeByStudentId(id);
        Iterable<ScienceGrade> scienceGrades = scienceGradeDao.findGradeByStudentId(id);
        Iterable<HistoryGrade> historyGrades = historyGradeDao.findGradeByStudentId(id);

        List<Grade> mathGradeList = new ArrayList<>();
        mathGrades.forEach(mathGradeList::add);

        List<Grade> scienceGradeList = new ArrayList<>();
        scienceGrades.forEach(scienceGradeList::add);

        List<Grade> historyGradeList = new ArrayList<>();
        historyGrades.forEach(historyGradeList::add);

        studentGrades.setHistoryGradeResults(historyGradeList);
        studentGrades.setMathGradeResults(mathGradeList);
        studentGrades.setScienceGradeResults(scienceGradeList);

        GradebookCollegeStudent gradebookCollegeStudent = new GradebookCollegeStudent(
                id,
                student.get().getFirstname(),
                student.get().getLastname(),
                student.get().getEmailAddress(),
                studentGrades);

        return gradebookCollegeStudent;
    }

    public void configStudentInfoModel(int id, Model m){
        GradebookCollegeStudent gradebookCollegeStudent = this.getStudentInfo(id);

        m.addAttribute("student", gradebookCollegeStudent);
        if(gradebookCollegeStudent.getStudentGrades().getMathGradeResults().size() > 0){
            m.addAttribute("mathAverage", gradebookCollegeStudent
                    .getStudentGrades().findGradePointAverage(
                            gradebookCollegeStudent.getStudentGrades().getMathGradeResults()
                    ));
        }else{
            m.addAttribute("mathAverage", "N/A");
        }

        if(gradebookCollegeStudent.getStudentGrades().getScienceGradeResults().size() > 0){
            m.addAttribute("scienceAverage", gradebookCollegeStudent
                    .getStudentGrades().findGradePointAverage(
                            gradebookCollegeStudent.getStudentGrades().getScienceGradeResults()
                    ));
        }else{
            m.addAttribute("scienceAverage", "N/A");
        }

        if(gradebookCollegeStudent.getStudentGrades().getHistoryGradeResults().size() > 0){
            m.addAttribute("historyAverage", gradebookCollegeStudent
                    .getStudentGrades().findGradePointAverage(
                            gradebookCollegeStudent.getStudentGrades().getHistoryGradeResults()
                    ));
        }else{
            m.addAttribute("historyAverage", "N/A");
        }
    }
}
