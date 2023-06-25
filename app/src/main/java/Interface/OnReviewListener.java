package Interface;

public interface OnReviewListener {
    void onCommentChanged(int position, String comment);
    void onRatingChanged(int position, float rating);
}
