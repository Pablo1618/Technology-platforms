using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Navigation;
using System.Windows.Shapes;
using System.Windows.Forms;
using System.Windows.Controls;
using System.Diagnostics;
using System.IO;
using System.Xml.Linq;
using static System.Windows.Forms.VisualStyles.VisualStyleElement;

namespace Lab8
{
    /// <summary>
    /// Interaction logic for MainWindow.xaml
    /// </summary>

    public partial class MainWindow : System.Windows.Window
    {
        TreeViewItem __rootNode = null;
        string __rootPath = null;

        public MainWindow()
        {
            InitializeComponent();
        }

        private void ExitApplication(object sender, RoutedEventArgs e)
        {
            System.Windows.Application.Current.Shutdown();
        }

        private void OpenButonClick(object sender, RoutedEventArgs e)
        {
            // Wyswietlanie okienka dialogowego
            var dlg = new FolderBrowserDialog() { Description = "Wybierz sciezke" };
            dlg.ShowDialog();


            // Wybrana sciezka
            __rootPath = dlg.SelectedPath;

            // Struktura drzewa TreeView
            __rootNode = new TreeViewItem();
            // Header - nazwa pliku
            // Tag - sciezka pliku
            __rootNode.Header = System.IO.Path.GetFileName(__rootPath);
            __rootNode.Tag = __rootPath;

            AddDirectoriesToTreeView(__rootPath, __rootNode);

            TreeViewBox.Items.Clear();
            TreeViewBox.Items.Add(__rootNode);

        }

        private void AddDirectoriesToTreeView(string directoryPath, TreeViewItem parentNode)
        {
            parentNode.Items.Clear();
            // Dodanie podfolderów do drzewa
            string[] directories = System.IO.Directory.GetDirectories(directoryPath);
            foreach (string directory in directories)
            {
                // Tworzenie nowego elementu dla podfolderu
                TreeViewItem node = new TreeViewItem();
                node.Header = System.IO.Path.GetFileName(directory);
                node.Tag = directory;

                AddContextMenuToTreeViewItem(node);

                // Rekurencyjne dodawanie pozostałych
                AddDirectoriesToTreeView(directory, node);

                parentNode.Items.Add(node);
            }

            // Dodanie plików do drzewa
            string[] files = System.IO.Directory.GetFiles(directoryPath);
            foreach (string file in files)
            {
                // Tworzenie nowego elementu dla pliku
                TreeViewItem fileNode = new TreeViewItem();
                fileNode.Header = System.IO.Path.GetFileName(file);
                fileNode.Tag = file;

                // Dodawanie menu kontekstowego do TreeViewItem
                AddContextMenuToTreeViewItem(fileNode);

                parentNode.Items.Add(fileNode);
            }
        }

        private void AddContextMenuToTreeViewItem(TreeViewItem item)
        {
            bool isDirectory = false;
            ContextMenu contextMenu = new ContextMenu();

            // Opcja Create - tylko dla folderow
            if (Directory.Exists(item.Tag.ToString())) // Tag - to sciezka
            {
                isDirectory = true;
                MenuItem createMenuItem = new MenuItem { Header = "Create" };
                //createMenuItem.Click += (sender, e) => CreateDirectory(item.Tag.ToString());
                createMenuItem.Click += (sender, e) =>
                {
                    //var window = new CreateNewFileWindow(item.Tag.ToString());
                    //var directoryInfo = item.Tag as DirectoryInfo;
                    var window = new CreateNewFileWindow(new DirectoryInfo(item.Tag.ToString()));
                    window.ShowDialog();
                    // Odswiezenie TreeView po dodaniu nowego pliku
                    AddDirectoriesToTreeView(__rootPath, __rootNode);
                    TreeViewBox.Items.Clear();
                    TreeViewBox.Items.Add(__rootNode);
                };

                contextMenu.Items.Add(createMenuItem);
            }

            // Opcja do otwierania pliku
            MenuItem openMenuItem = new MenuItem { Header = "Open" };
            // jesli isDirectory==false to otwarcie pliku wyswietli go z prawej strony
            // jesli isDirectory==true to otwarcie spowoduje otworzenie folderu w windows 
            openMenuItem.Click += (sender, e) => OpenFile(isDirectory, item.Tag.ToString());
            contextMenu.Items.Add(openMenuItem);

            // Opcja do usuniecia pliku
            MenuItem deleteMenuItem = new MenuItem { Header = "Delete" };
            deleteMenuItem.Click += (sender, e) => DeleteFile(item); // Caly item aby moc usunac zarowno TreeView item jak i rzeczywisty plik na dysku
            contextMenu.Items.Add(deleteMenuItem);

            // Przypisanie ContextMenu do TreeViewItem
            item.ContextMenu = contextMenu;
        }

        private void OpenFile(bool isDirectory, string filePath)
        {
            if (isDirectory)
            {
                // Otwarcie folderu
                Process p = new Process();
                p.StartInfo.UseShellExecute = true;
                p.StartInfo.FileName = filePath;
                p.Start();
            }
            else
            {
                // Wyswietlenie zawartosci pliku po prawej stronie w okienku
                TextBoxForFileViewing.Text = File.ReadAllText(filePath, Encoding.UTF8);
            }
        }

        private void DeleteFile(TreeViewItem itemToRemove)
        {
            string filePath = itemToRemove.Tag.ToString();

            // Sprawdzanie, czy ścieżka wskazuje na plik czy folder
            if (File.Exists(filePath))
            {
                // Usuwanie atrybutu ReadOnly, jeśli jest ustawiony
                FileAttributes attributes = File.GetAttributes(filePath);
                if ((attributes & FileAttributes.ReadOnly) == FileAttributes.ReadOnly)
                {
                    File.SetAttributes(filePath, attributes & ~FileAttributes.ReadOnly);
                }

                File.Delete(filePath);
            }
            else if (Directory.Exists(filePath))
            {
                // Usuwanie atrybutu ReadOnly, jeśli jest ustawiony
                DirectoryInfo directoryInfo = new DirectoryInfo(filePath);
                if ((directoryInfo.Attributes & FileAttributes.ReadOnly) == FileAttributes.ReadOnly)
                {
                    directoryInfo.Attributes = directoryInfo.Attributes & ~FileAttributes.ReadOnly;
                }

                // Usuwanie folderu i jego zawartości
                Directory.Delete(filePath, true);
            }

            // Usuwanie obiektu item z TreeView
            ItemsControl parent = ItemsControl.ItemsControlFromItemContainer(itemToRemove);
            if (parent != null)
            {
                parent.Items.Remove(itemToRemove);
            }
        }

        private void TreeViewSelectItem(object sender, RoutedPropertyChangedEventArgs<object> e)
        {
            if (TreeViewBox.SelectedItem is TreeViewItem selectedItem)
            {
                // Sprawdzenie czy to plik (a nie folder)
                if (selectedItem.Tag is string filePath)
                {

                    FileAttributes attributes = File.GetAttributes(filePath);
                    string attributeString = GetDOSAttributesString(attributes);

                    AttributesTextBlock.Text = attributeString;
                }
            }
        }

        private string GetDOSAttributesString(FileAttributes attributes)
        {
            string attributeString = "";

            if ((attributes & FileAttributes.ReadOnly) == FileAttributes.ReadOnly)
            {
                attributeString += "r";
            }
            else
            {
                attributeString += "-";
            }

            if ((attributes & FileAttributes.Archive) == FileAttributes.Archive)
            {
                attributeString += "a";
            }
            else
            {
                attributeString += "-";
            }

            if ((attributes & FileAttributes.System) == FileAttributes.System)
            {
                attributeString += "s";
            }
            else
            {
                attributeString += "-";
            }

            if ((attributes & FileAttributes.Hidden) == FileAttributes.Hidden)
            {
                attributeString += "h";
            }
            else
            {
                attributeString += "-";
            }

            return attributeString;
        }

    }
}
