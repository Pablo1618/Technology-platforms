using System.IO;
using System.Text.RegularExpressions;
using System.Windows;
using MessageBox = System.Windows.MessageBox;

namespace Lab8;

public partial class CreateNewFileWindow : Window
{
    DirectoryInfo __chosenPath = null;
    public CreateNewFileWindow(DirectoryInfo path)
    {
        __chosenPath = path;
        InitializeComponent();
    }

    private void CreateNewFile(object sender, RoutedEventArgs e)
    {
        var name = NameTextBox.Text;
        var isFileBoxChecked = FileType.IsChecked;
        var attributes = FileAttributes.Normal;

        if (IsReadOnly.IsChecked == true)
            attributes |= FileAttributes.ReadOnly;

        if (IsArchive.IsChecked == true)
            attributes |= FileAttributes.Archive;

        if (IsHidden.IsChecked == true)
            attributes |= FileAttributes.Hidden;

        if (IsSystem.IsChecked == true)
            attributes |= FileAttributes.System;

        if (isFileBoxChecked == true && !Regex.IsMatch(name, @"^[a-zA-Z0-9_~-]{1,8}\.(txt|php|html)$"))


        {
            MessageBox.Show("Zla nazwa! Nazwa musi miec 1-8 znakow i posiadac rozszerzenie txt,php lub html", "Blad", MessageBoxButton.OK, MessageBoxImage.Error);
        }
        else
        {
            if (isFileBoxChecked == false)
            {
                Directory.CreateDirectory(Path.Combine(__chosenPath.FullName, name));
            }
            else
            {
                File.Create(Path.Combine(__chosenPath.FullName, name)).Close();
                File.SetAttributes(Path.Combine(__chosenPath.FullName, name), attributes);
            }
        }
        Close();
    }

    private void CancelCreatingNewFile(object sender, RoutedEventArgs e)
    {
        Close();
    }
}
