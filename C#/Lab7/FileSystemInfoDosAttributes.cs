using System.IO;

public static class FileSystemInfoDosAttributes
{
    public static string GetDosAttributes(this FileSystemInfo fileInfo)
    {
        string attributes = "";

        if ((fileInfo.Attributes & FileAttributes.ReadOnly) != 0)
            attributes += "r";
        else
            attributes += "-";

        if ((fileInfo.Attributes & FileAttributes.Archive) != 0)
            attributes += "a";
        else
            attributes += "-";

        if ((fileInfo.Attributes & FileAttributes.Hidden) != 0)
            attributes += "h";
        else
            attributes += "-";

        if ((fileInfo.Attributes & FileAttributes.System) != 0)
            attributes += "s";
        else
            attributes += "-";

        return attributes;
    }
}
