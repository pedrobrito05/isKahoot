package iskahoot.model;


import java.util.List;

public class Quiz {
    public String name;
    public List<Question> questions;

    public Question getQuestion(int index){
        return questions.get(index);
    }

    public Quiz(String name, List<Question> questions, int limit){
        this.name=name;
        this.questions=questions.stream().limit(limit).toList();
    }
    public int numberOfQuestions(){
        return questions.size();
    }
}