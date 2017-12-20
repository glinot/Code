using GalaSoft.MvvmLight;
using GalaSoft.MvvmLight.Command;
using GalaSoft.MvvmLight.Messaging;
using System.Collections.ObjectModel;
using System.IO;
using System.Linq;
using System.Reflection;

namespace INSAWorld.ViewModel
{
    public class LoadGameViewModel : ViewModelBase
    {
        private string directoryPath;

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
                SelectGameCommand.RaiseCanExecuteChanged();
            }
        }

        public ObservableCollection<string> GameFilenames { get; }

        public RelayCommand SelectGameCommand { get; }

        public RelayCommand BackCommand { get; }

        public LoadGameViewModel()
        {
            directoryPath = $"{Path.GetDirectoryName(Assembly.GetExecutingAssembly().Location)}/Saves";
            if (Directory.Exists(directoryPath))
                GameFilenames = new ObservableCollection<string>(Directory.GetFiles(directoryPath, "*.o").Select(Path.GetFileNameWithoutExtension));

            SelectGameCommand = new RelayCommand(() =>
            {
                Messenger.Default.Send(new NavigationViewMessage(new GameViewModel()));
                Messenger.Default.Send(new GameMessage(
                    SavedGameBuilder.Create()
                    .SetPath($"{directoryPath}/{SelectedGame}.o")
                    .Build(), false
                ));
            }, () => {
                return !string.IsNullOrWhiteSpace(SelectedGame);
            });

            BackCommand = new RelayCommand(() =>
            {
                Messenger.Default.Send(new NavigationSubviewMessage(new MainMenuViewModel()));
            });
        }
    }
}