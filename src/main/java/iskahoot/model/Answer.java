package iskahoot.model;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Answer implements Serializable {
    private int answer;
    private long time;

    public Answer(int answer, long time){
        this.answer=answer;
        this.time=time;
    }

    public int getAnswer(){
        return answer;
    }
    public long getTime(){
        return time;
    }

}
