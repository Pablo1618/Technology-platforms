using System;
using System.IO;

public static class DirectoryInfoOldestDateRecursive
{
    public static DateTime GetOldestDateRecursive(this DirectoryInfo directory)
    {
        DateTime oldestDate = DateTime.MaxValue;

        // Data plikow w katalogu
        foreach (var file in directory.GetFiles())
        {
            if (file.LastWriteTime < oldestDate)
            {
                oldestDate = file.LastWriteTime;
            }
        }

        // Data plikow dla podkatalogow
        foreach (var subdirectory in directory.GetDirectories())
        {
            DateTime subdirectoryOldestDate = subdirectory.GetOldestDateRecursive();
            if (subdirectoryOldestDate < oldestDate)
            {
                oldestDate = subdirectoryOldestDate;
            }
        }

        return oldestDate;
    }


}
