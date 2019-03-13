package com.lweyn.sparkreader.ui;

import com.lweyn.sparkreader.Main;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;


/**
 * Holds a 'page' of text, or a set of {@link Line}s
 */
public class Page implements Iterable<Line>
{
    private ArrayList<Line> lines;
    private String text = "";
    private int longestLine = 0;

    public Page(String text)
    {
        this();
        setText(text);
    }
    public Page()
    {
        lines = new ArrayList<>();
        lines.add(new Line());
    }
    //TODO aim to make page 'immutable', currently here for easy porting
    public void setText(String newText)
    {
        this.text = newText;
        String bits[] = newText.split("\n");
        longestLine = 0;
        int i = 0;
        for(String bit:bits)
        {
            if(bit.length() > longestLine)longestLine = bit.length();

            if(i == lines.size())
            {
                lines.add(new Line(Main.splitter.split(bit, new HashSet<>())));
            }
            else
            {
                lines.get(i).setWords(Main.splitter.split(bit, lines.get(i).getMarkers()));
            }
            i++;
        }
        //clear all leftover lines
        while(i < lines.size())
        {
            lines.remove(i);
        }
        //reflow if needed
        //TODO get reflow working
        /*int maxLineLength = Main.options.getOptionInt("windowWidth") / mainFontSize;
        if(longestLine > mainFontSize && Main.options.getOptionBool("reflowToFit"))
        {
            ArrayList<Line> newLines = new ArrayList<>(lines.size());
            for(Line line:lines)
            {
                Line newLine = new Line();
                for(FoundWord word:line.getDisplayedWords())
                {
                    if(newLine.calcCharLength() + word.getTextLength() > maxLineLength)
                    {
                        newLines.add(newLine);
                        newLine = new Line();
                    }
                    addWord(line, newLine, word);
                }
                if(newLine.calcCharLength() != 0)newLines.add(newLine);
            }
            lines = newLines;
        }*/
    }

    public String getText()
    {
        return text;
    }

    public void clearMarkers()
    {
        for(Line line:lines)
        {
            line.getMarkers().clear();
        }
    }

    public Line getLine(int line)
    {
        return lines.get(line);
    }

    public int getLineCount()
    {
        return lines.size();
    }
    public int getMaxTextLength()
    {
        return longestLine;
    }

    @Override
    public Iterator<Line> iterator()
    {
        return lines.iterator();
    }
}