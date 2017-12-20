using GalaSoft.MvvmLight;

namespace INSAWorld.ViewModel
{
    public class SaveGameDialogViewModel : ViewModelBase
    {
        public string filename;
        public string Filename
        {
            get
            {
                return filename;
            }

            set
            {
                if (value != filename)
                {
                    filename = value;
                    RaisePropertyChanged("Filename");
                    RaisePropertyChanged("SaveEnable");
                }
            }
        }

        public bool SaveEnable
        {
            get
            {
                return !string.IsNullOrWhiteSpace(filename);
            }
        }
    }
}
