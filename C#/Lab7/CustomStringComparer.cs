[Serializable]
class CustomStringComparer : IComparer<string>
{
    public int Compare(string x, string y)
    {
        int lengthComparison = x.Length.CompareTo(y.Length);
        if (lengthComparison == 0)
        {
            // Sortowanie alfabetyczne
            return string.Compare(x, y, StringComparison.Ordinal);
        }
        return lengthComparison;
    }
}