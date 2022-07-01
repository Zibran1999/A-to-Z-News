package dailynews.localandglobalnews.models;

import java.util.List;

public class QuizModelList {
    List<QuizModel> data =null;

    public QuizModelList(List<QuizModel> data) {
        this.data = data;
    }

    public List<QuizModel> getData() {
        return data;
    }
}
