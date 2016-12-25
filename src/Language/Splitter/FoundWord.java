/* 
 * Copyright (C) 2016 Laurens Weyn
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package Language.Splitter;

import Language.Dictionary.Japanese;
import UI.UI;
import java.awt.Graphics2D;
import java.util.ArrayList;

/**
 * Holds a word from the text and definitions that match it
 * @author laure
 */
public class FoundWord
{
    private final String text;//text to show
    private final ArrayList<FoundDef> definitions;//known meanings
    private final int startX, endX;//start and end points in sentence (for rendering)
    
    private int currentDef = 0;//current definition to render
    
    private boolean showDef = false;
    
    
    
    public FoundWord(String text, ArrayList<FoundDef> definitions, int startX, int endX)
    {
        this.text = text;
        this.definitions = definitions;
        this.startX = startX;
        this.endX = endX;
        
        if(definitions != null)definitions.sort(null);
        /*int bestScore = Integer.MIN_VALUE;//take best scoreing word as the default definition
        if(definitions == null)return;
        for (int i = 0; i < definitions.size(); i++)
        {
            if(definitions.get(i).getScore() > bestScore)
            {
                bestScore = definitions.get(i).getScore();
                currentDef = i;
            }
        }*/
    }
    public FoundWord(String text, int startX, int endX)
    {
        this.text = text;
        definitions = new ArrayList<>();
        this.startX = startX;
        this.endX = endX;
        if(definitions != null)definitions.sort(null);
    }
    public void addDefinition(FoundDef def)
    {
        definitions.add(def);
    }
    public void sortDefs()
    {
        definitions.sort(null);
    }
    public int getDefinitionCount()
    {
        return definitions.size();
    }
    
    public void render(Graphics2D g, int xOffset)
    {
        UI.options.getFontAA(g, "textFont");
        g.setClip(0, 0, UI.windowWidth, UI.maxHeight);//render only over window
        g.setFont(UI.textFont);
        int startPos = g.getFontMetrics().charWidth('べ') * startX + xOffset;
        int width = g.getFontMetrics().charWidth('べ') * text.length();
        boolean known = isKnown();
        
        g.setColor(known? UI.knownTextBackCol:UI.textBackCol);
        g.fillRect(startPos + 1, UI.textStartY, width - 2, g.getFontMetrics().getHeight());
        g.setColor(UI.textCol);
        g.drawString(text, startPos, UI.textStartY + g.getFontMetrics().getMaxAscent());
        
        if(definitions == null)return;//don't bother rendering furigana/defs if we don't know it
        String furiText = "";
        if(showDef)
        {
            furiText = (currentDef + 1) + "/" + definitions.size();
        }
        else if(UI.showFurigana && Japanese.hasKanji(text) && !known)
        {
            furiText = definitions.get(currentDef).getFurigana();
        }
        g.setFont(UI.furiFont);
        UI.options.getFontAA(g, "furiFont");
        g.setColor(UI.furiCol);
        int furiX = startPos + width/2 - g.getFontMetrics().stringWidth(furiText)/2;
        g.drawString(furiText, furiX, UI.furiganaStartY + g.getFontMetrics().getAscent());
        
        if(showDef)
        {
            g.setClip(null);//render this anywhere
            g.setFont(UI.defFont);
            UI.options.getFontAA(g, "defFont");
            int y = UI.defStartY + g.getFontMetrics().getAscent();
            definitions.get(currentDef).render(g, startPos, Math.max(width, UI.defWidth), y);
        }
    }
    public void toggleWindow(int pos)
    {
        if(inBounds(pos))
        {
            showDef = !showDef;
        }else showDef = false;
    }
    public void showDef(boolean mode)
    {
        showDef = mode;
    }
    public boolean inBounds(int xPos)
    {
        return xPos >= startX && xPos < endX;
    }
    public String getText()
    {
        return text;
    }

    @Override
    public String toString()
    {
        if(definitions == null)return text;
        else return "" + definitions.get(currentDef);
    }
    public boolean isKnown()
    {
        return UI.known.isKnown(this);
    }
    public boolean isShowingDef()
    {
        return showDef;
    }
    public FoundDef getCurrentDef()
    {
        if(definitions == null)return null;
        return definitions.get(currentDef);
    }
    public void scrollDown()
    {
        if(definitions == null)return;
        currentDef = Math.min(currentDef + 1, definitions.size() - 1);
    }
    public void scrollUp()
    {
        if(definitions == null)return;
        currentDef = Math.max(currentDef - 1, 0);
    }

    public ArrayList<FoundDef> getFoundDefs()
    {
        return definitions;
    }

    public int startX()
    {
        return startX;
    }
    public int endX()
    {
        return endX;
    }
    
}
