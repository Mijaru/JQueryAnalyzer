package Components.MainWindowComponents.Highlighter;

import javax.swing.text.Style;

public class SpecialStyle
{
    public String name;   
    public Style style;
    
    public SpecialStyle(String name, Style style)
    {
        setName(name);
        setStyle(style);
    }
    
    /**
     * @return Returns the name.
     */
    public String getName()
    {
        return name;
    }
    /**
     * @param name The name to set.
     */
    public void setName(String name)
    {
        this.name = name;
    }
    /**
     * @return Returns the style.
     */
    public Style getStyle()
    {
        return style;
    }
    /**
     * @param style The style to set.
     */
    public void setStyle(Style style)
    {
        this.style = style;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null)
            return false;
        
        if (obj == this)
            return true;
        
        if (!(obj instanceof SpecialStyle))
            return false;
                
        return getName().equals(((SpecialStyle)obj).getName());
    }

    @Override
    public int hashCode()
    {
        int hash = 0;
        for (byte c : getName().getBytes())
            hash += c; 
            
        return hash;
    }

    @Override
    public String toString()
    {
        return getName();
    }
    
}
