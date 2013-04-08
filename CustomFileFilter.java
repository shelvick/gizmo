import javax.swing.filechooser.FileFilter;
import java.io.File;

public class CustomFileFilter extends FileFilter
{	
	public boolean accept(File f)
	{
		if ("swap".equals(getExtension(f)))
			return true;
		else
			return false;
	}
	
	public static String getExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 &&  i < s.length() - 1) {
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
    }
	
	public String getDescription()
	{
		return "Labor Swapping File";
	}
}