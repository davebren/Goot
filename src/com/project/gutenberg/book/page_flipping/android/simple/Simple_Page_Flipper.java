package com.project.gutenberg.book.page_flipping.android.simple;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import com.project.gutenberg.R;
import com.project.gutenberg.book.Book;
import com.project.gutenberg.book.Chapter;
import com.project.gutenberg.book.page_flipping.Page_Flipper;
import com.project.gutenberg.book.view.Page_View;
import com.project.gutenberg.book.view.android.Android_Book_View;
import com.project.gutenberg.util.Debug;

public class Simple_Page_Flipper extends Page_Flipper {
    final Button right_button;
    final Button left_button;
    final Button_Animator right_button_animator = new Button_Animator();
    final Button_Animator left_button_animator = new Button_Animator();
    final Button_Animation_Listener left_animation_listener = new Button_Animation_Listener();
    final Button_Animation_Listener right_animation_listener = new Button_Animation_Listener();

    public Simple_Page_Flipper(Android_Book_View book_view, Context context, Page_View prev_page, Page_View current_page, Page_View next_page, Book book) {
        super(book_view, prev_page, current_page, next_page, book);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View buttons = inflater.inflate(R.layout.simple_page_flipper_buttons,null);
        right_button = (Button)buttons.findViewById(R.id.simple_page_flipper_right);
        left_button = (Button)buttons.findViewById(R.id.simple_page_flipper_left);
        LinearLayout.LayoutParams fill_screen_params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        book_view.get_page_holder().addView(buttons,fill_screen_params);
        right_animation_listener.set_view(right_button);
        left_animation_listener.set_view(left_button);
        right_button_animator.get_fade_out().setAnimationListener(right_animation_listener);
        left_button_animator.get_fade_out().setAnimationListener(left_animation_listener);
        right_button.startAnimation(right_button_animator.get_fade_out());
        left_button.startAnimation(left_button_animator.get_fade_out());
        right_button.setOnTouchListener(right_button_listener);
        left_button.setOnTouchListener(left_button_listener);
    }
    public void next_page() {
        Chapter current_chapter = book.get_chapter(book.get_current_chapter());
        if (current_chapter.number_of_pages() > current_chapter.get_list_relative_current_page_index()+1) {
            current_page.remove_view();
            Debug.log("remove view");
            current_page.add_view(0);
            Debug.log("add view back");
            Debug.log("pre swap: " + prev_page.get_page_stack_id() + ", " + current_page.get_page_stack_id() + ", " + next_page.get_page_stack_id());
            Page_View temp = current_page;
            current_page = next_page;
            next_page = temp;
            Debug.log("post swap: " + prev_page.get_page_stack_id() + ", " + current_page.get_page_stack_id() + ", " + next_page.get_page_stack_id());
            String[][] lines_of_text = new String[3][];
            lines_of_text[0] = book_view.get_page_lines(1);
            lines_of_text[1] = current_chapter.get_next_page().get_page_text();
            if (current_chapter.number_of_pages() > current_chapter.get_list_relative_current_page_index()+1) {
                lines_of_text[2] = current_chapter.peek_next_page().get_page_text();
            } else if (!current_chapter.last_page_loaded) {
                lines_of_text[2] = new String[1];
                lines_of_text[2][0] = "";
                current_chapter.add_loading_hook(1, true, book_view);
            }
            book_view.set_prev_current_next_page_lines(lines_of_text);
            current_page.set_page_stack_id(0);
            next_page.set_page_stack_id(1);
            book_view.set_prev_current_next_page_lines(lines_of_text);
        } else if (current_chapter.last_page_loaded) {
            if (book.get_current_chapter()+1 < book.number_of_chapters()) {

            } else {

            }
        } else {

        }

    }
    public void prev_page() {

    }
    private View.OnTouchListener right_button_listener = new View.OnTouchListener(){
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                if (!left_animation_listener.is_animating())
                    right_button.setAlpha(0.35f);
            } else if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
                if (!right_animation_listener.is_animating()) {
                    right_button.setAlpha(0f);
                    next_page();
                }
            }
            return false;
        }
    };
    private View.OnTouchListener left_button_listener = new View.OnTouchListener(){
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                if (!left_animation_listener.is_animating())
                    left_button.setAlpha(0.35f);
            } else if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
                if (!left_animation_listener.is_animating()) {
                    left_button.setAlpha(0f);
                    prev_page();
                }
            }
            return false;
        }
    };
}
