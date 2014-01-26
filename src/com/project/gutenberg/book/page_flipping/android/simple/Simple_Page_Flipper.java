package com.project.gutenberg.book.page_flipping.android.simple;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.project.gutenberg.R;
import com.project.gutenberg.book.Book;
import com.project.gutenberg.book.Chapter;
import com.project.gutenberg.book.page_flipping.Page_Flipper;
import com.project.gutenberg.book.view.Page_View;
import com.project.gutenberg.book.view.android.Android_Book_View;

public class Simple_Page_Flipper extends Page_Flipper {
    final Button right_button;
    final Button left_button;
    final Button_Animator right_button_animator = new Button_Animator();
    final Button_Animator left_button_animator = new Button_Animator();
    final Button_Animation_Listener left_animation_listener = new Button_Animation_Listener();
    final Button_Animation_Listener right_animation_listener = new Button_Animation_Listener();
    Context context;

    public Simple_Page_Flipper(Android_Book_View book_view, Context context, Page_View prev_page, Page_View current_page, Page_View next_page, Book book) {
        super(book_view, prev_page, current_page, next_page, book);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
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
        Chapter current_chapter = book.get_current_chapter();
        if (book.on_last_chapter() && book.get_current_chapter().on_last_page()) return;
        current_page.remove_view();
        Page_View temp = prev_page;
        prev_page = current_page;
        current_page = next_page;
        next_page = temp;
        next_page.add_view(0);
        String[][] lines_of_text = new String[3][];
        lines_of_text[0] = current_chapter.peek_current_page().get_page_text();
        if (current_chapter.on_penultimate_page()) {
            lines_of_text[1] = current_chapter.next_page().get_page_text();
            if (!book.on_last_chapter()) {
                lines_of_text[2] = book.peek_next_chapter().peek_current_page().get_page_text();
            } else lines_of_text[2] = new String[0];
        } else if (current_chapter.on_last_page()) {
            book.next_chapter();
            ((Android_Book_View)book_view).get_action_bar_handler().set_chapter_title(book.get_current_chapter_index());
            lines_of_text[1] = book.get_current_chapter().peek_current_page().get_page_text();
            Toast.makeText(context,book.get_current_chapter().get_title(), 2500).show();
            if (!book.get_current_chapter().on_last_page()) {
                lines_of_text[2] = book.get_current_chapter().peek_next_page().get_page_text();
            } else lines_of_text[2] = new String[0];
        } else {
            lines_of_text[1] = current_chapter.next_page().get_page_text();
            lines_of_text[2] = current_chapter.peek_next_page().get_page_text();
        }
        prev_page.set_page_stack_id(-1);
        current_page.set_page_stack_id(0);
        next_page.set_page_stack_id(1);
        book_view.set_prev_current_next_page_lines(lines_of_text);
        ((Android_Book_View)book_view).get_action_bar_handler().set_page(book.get_page_number());
    }
    public void prev_page() {
        Chapter current_chapter = book.get_current_chapter();
        if (current_chapter.on_first_page() && book.on_first_chapter()) return;
        next_page.remove_view();
        prev_page.add_view(1);
        Page_View temp = current_page;
        current_page = prev_page;
        prev_page = next_page;
        next_page = temp;
        String[][] lines_of_text = new String[3][];
        lines_of_text[2] = current_chapter.peek_current_page().get_page_text();
        if (current_chapter.on_second_page()) {
            lines_of_text[1] = current_chapter.previous_page().get_page_text();
            if (!book.on_first_chapter()) {
                lines_of_text[0] = book.peek_previous_chapter().peek_last_page().get_page_text();
            } else lines_of_text[0] = new String[0];
        } else if (current_chapter.on_first_page()) {
            book.previous_chapter();
            book.get_current_chapter().set_last_page();
            lines_of_text[1] = book.get_current_chapter().peek_current_page().get_page_text();
            ((Android_Book_View)book_view).get_action_bar_handler().set_chapter_title(book.get_current_chapter_index());
            if (!book.get_current_chapter().on_first_page()) {
                lines_of_text[0] = book.get_current_chapter().peek_previous_page().get_page_text();
            } else lines_of_text[0] = new String[0];
        } else {
            lines_of_text[1] = current_chapter.previous_page().get_page_text();
            lines_of_text[0] = current_chapter.peek_previous_page().get_page_text();
        }
        prev_page.set_page_stack_id(-1);
        current_page.set_page_stack_id(0);
        next_page.set_page_stack_id(1);
        book_view.set_prev_current_next_page_lines(lines_of_text);
        ((Android_Book_View)book_view).get_action_bar_handler().set_page(book.get_page_number());
    }
    public void jump_to_chapter(int chapter_index) {
        String[][] lines_of_text = new String[3][];
        book.get_current_chapter().set_first_page();
        book.set_current_chapter(chapter_index);
        Chapter current_chapter = book.get_current_chapter();
        current_chapter.set_first_page();
        lines_of_text[1] = current_chapter.peek_current_page().get_page_text();
        if (!current_chapter.on_last_page()) {
            lines_of_text[2] = current_chapter.peek_next_page().get_page_text();
        } else if (!book.on_last_chapter()) {
            lines_of_text[2] = book.peek_next_chapter().peek_current_page().get_page_text();
        } else {
            lines_of_text[2] = new String[0];
        }
        if (!book.on_first_chapter()) {
            lines_of_text[0] = book.peek_previous_chapter().peek_last_page().get_page_text();
        } else {
            lines_of_text[0] = new String[0];
        }
        book_view.set_prev_current_next_page_lines(lines_of_text);
        current_page.remove_view();
        next_page.remove_view();
        current_page.add_view(0);
        next_page.add_view(0);
        prev_page.set_page_stack_id(-1);
        current_page.set_page_stack_id(0);
        next_page.set_page_stack_id(1);
        ((Android_Book_View)book_view).get_action_bar_handler().set_page(book.get_page_number());
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
