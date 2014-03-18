package pl.fracz.mcr.source;

/*
 2013-10-23, fracz, first implementation
 2013-10-30, fracz, added syntax highlighting
 2014-02-26, fracz, added ability to add voice comment
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Html;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.io.Serializable;
import java.util.List;

import pl.fracz.mcr.comment.Comment;
import pl.fracz.mcr.comment.CommentNotAddedException;
import pl.fracz.mcr.comment.TextComment;
import pl.fracz.mcr.comment.VoiceComment;

/**
 * View that represents one line of code.
 */
@SuppressLint("ViewConstructor")
public class Line extends LinearLayout implements Serializable {
    private static final long serialVersionUID = 3076583280108678995L;
    private static final int TWO = 2;
 
    private final int _lineNumber;
 
    private final String _lineOfCode;

    // holds the line number
    private final TextView lineNumberView;

    private final TextView lineContent;

    private final SourceFile sourceFile;

    private List<Comment> comments;

    public Line(Context context, SourceFile sourceFile, int lineNumber,
                String lineOfCode, boolean syntaxColor) {
        super(context);
        this.sourceFile = sourceFile;
        this._lineNumber = lineNumber;
        this._lineOfCode = lineOfCode;
        setOrientation(LinearLayout.HORIZONTAL);

        lineNumberView = new TextView(getContext());
        lineNumberView.setText(String.format("%d.", lineNumber););
        lineNumberView.setSingleLine();
        lineNumberView.setWidth(30);
        addView(lineNumberView);

        TextView lineContent = new TextView(getContext());
        addLineContent(syntaxColor);

        this.comments = sourceFile.getComments().getComments(this);
    }

    public int get() {
        return _lineNumber;
    }

    /**
     * Adds a text comment.
     *
     * @param comment
     * @throws CommentNotAddedException
     */
    public void addTextComment(String comment) throws CommentNotAddedException {
        sourceFile.getComments().addComment(this, new TextComment(comment));
        this.comments = sourceFile.getComments().getComments(this);
        if (comments.size() > 0) {
            lineNumberView.setBackgroundColor(Color.parseColor("#008000"));
        }
    }

    /**
     * Adds a voice comment.
     *
     * @param recordedFile
     * @throws CommentNotAddedException
     */
    public void createVoiceComment(File recodedFile) throws CommentNotAddedException {
        sourceFile.getComments().addComment(this, new VoiceComment(recodedFile));
        this.comments = sourceFile.getComments().getComments(this);
        if (comments.size() > 0) {
            lineNumberView.setBackgroundColor(Color.parseColor("#008000"));
        }
    }

//    public void addVideoComment(File videoFile) throws CommentNotAddedException {
//
//    }

    private void addLineContent(boolean syntaxColor){
        if (!syntaxColor || !SyntaxHighlighter.canBeHighlighted(syntaxColor))
            lineContent.setText(Html.fromHtml(lineOfCode));
        else
            lineContent.setText(SyntaxHighlighter.highlight(Html.fromHtml(lineOfCode)));
        lineContent.setTypeface(Typeface.MONOSPACE);
        addView(lineContent);
    }

    public List<Comment> getComments() {
        return this.comments;
    }

    public boolean hasConversation(){
        sourceFile.markConversation(this);
        return getComments().size() > TWO;
    }
}
