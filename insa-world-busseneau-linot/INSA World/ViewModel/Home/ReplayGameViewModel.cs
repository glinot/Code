using GalaSoft.MvvmLight;
using GalaSoft.MvvmLight.Command;
using GalaSoft.MvvmLight.Messaging;
using System.Collections.ObjectModel;
using System.IO;
using System.Linq;
using System.Reflection;

namespace INSAWorld.ViewModel
{
    public class ReplayGameViewModel : ViewModelBase
    {
        private string directoryPath;

        private string selectedReplay;
        public string SelectedReplay
        {
            get
            {
                return selectedReplay;
            }

            set
            {
                if (value != selectedReplay)
                {
                    selectedReplay = value;
                    SelectReplayCommand.RaiseCanExecuteChanged();
                }
            }
        }

        public ObservableCollection<string> ReplayFilenames { get; }

        public RelayCommand SelectReplayCommand { get; }

        public RelayCommand BackCommand { get; }

        public ReplayGameViewModel()
        {
            directoryPath = $"{Path.GetDirectoryName(Assembly.GetExecutingAssembly().Location)}/Replays";
            if (Directory.Exists(directoryPath))
                ReplayFilenames = new ObservableCollection<string>(Directory.GetFiles(directoryPath, "*.o").Select(Path.GetFileNameWithoutExtension));

            SelectReplayCommand = new RelayCommand(() =>
            {
                
                Messenger.Default.Send(new NavigationViewMessage(new GameViewModel()));
                Messenger.Default.Send(new GameMessage(Game.LoadGame($"{directoryPath}/{SelectedReplay}.o"), true));
            }, () => {
                return !string.IsNullOrWhiteSpace(SelectedReplay);
            });

            BackCommand = new RelayCommand(() =>
            {
                Messenger.Default.Send(new NavigationSubviewMessage(new MainMenuViewModel()));
            });
        }
    }
}