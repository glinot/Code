using GalaSoft.MvvmLight;
using GalaSoft.MvvmLight.Command;
using GalaSoft.MvvmLight.Messaging;
using System.Windows;

namespace INSAWorld.ViewModel
{
    public class MainMenuViewModel : ViewModelBase
    {
        public RelayCommand NewGameCommand { get; }

        public RelayCommand LoadGameCommand { get; }

        public RelayCommand ReplayGameCommand { get; }

        public RelayCommand ExitCommand { get; }

        public MainMenuViewModel()
        {
            NewGameCommand  = new RelayCommand(() =>
            {
                Messenger.Default.Send(new NavigationSubviewMessage(new NewGameViewModel()));
            });

            LoadGameCommand = new RelayCommand(() =>
            {
                Messenger.Default.Send(new NavigationSubviewMessage(new LoadGameViewModel()));
            });

            ReplayGameCommand = new RelayCommand(() =>
            {
                Messenger.Default.Send(new NavigationSubviewMessage(new ReplayGameViewModel()));
            });

            ExitCommand = new RelayCommand(() =>
            {
                Application.Current.Shutdown();
            });
        }
    }
}