package iskahoot.model;


import java.util.List;

public class Quiz {
    public String name;
    public List<Question> questions;

    public Question getQuestion(int index){
        return questions.get(index);
    }
}