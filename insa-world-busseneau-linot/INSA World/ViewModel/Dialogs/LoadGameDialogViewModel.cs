using GalaSoft.MvvmLight;
using System.Collections.ObjectModel;
using System.IO;
using System.Linq;
using System.Reflection;

namespace INSAWorld.ViewModel
{
    public class LoadGameDialogViewModel : ViewModelBase
    {
        public string directoryPath;

        public string FilenamePath
        {
            get
            {
                return $"{directoryPath}/{selectedGame}.o";
            }
        }

        private string selectedGame;
        public string SelectedGame
        {
            get
            {
                return selectedGame;
            }

            set
            {
                selectedGame = value;
                RaisePropertyChanged("FilenamePath");
                RaisePropertyChanged("LoadEnable");
            }
        }

        public ObservableCollection<string> GameFilenames { get; }

        public bool LoadEnable
        {
            get
            {
                return !string.IsNullOrWhiteSpace(selectedGame);
            }
        }

        public LoadGameDialogViewModel()
        {
            directoryPath = $"{Path.GetDirectoryName(Assembly.GetExecutingAssembly().Location)}/Saves";
            if (Directory.Exists(directoryPath))
                GameFilenames = new ObservableCollection<string>(Directory.GetFiles(directoryPath, "*.o").Select(Path.GetFileNameWithoutExtension));
        }
    }
}
