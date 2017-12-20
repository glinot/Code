using GalaSoft.MvvmLight;

namespace INSAWorld.ViewModel.Dialogs
{
    public class WinnerDialogViewModel : ViewModelBase
    {
        public string WinnerName { get; }

        public WinnerDialogViewModel(string name)
        {
            WinnerName = name;
        }
    }
}
