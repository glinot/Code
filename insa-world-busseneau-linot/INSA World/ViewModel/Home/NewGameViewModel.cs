using GalaSoft.MvvmLight;
using GalaSoft.MvvmLight.Command;
using GalaSoft.MvvmLight.Messaging;
using System.Collections.ObjectModel;

namespace INSAWorld.ViewModel
{
    public class NewGameViewModel : ViewModelBase
    {
        private MapStrategy selectedMapType;
        public MapStrategy SelectedMapType
        {
            get
            {
                return selectedMapType;
            }

            set
            {
                if (value != selectedMapType)
                {
                    selectedMapType = value;
                    RaisePropertyChanged("SelectedMapType");
                    StartGameCommand.RaiseCanExecuteChanged();
                }
            }
        }

        public ObservableCollection<MapStrategy> MapTypes { get; }

        private string player1Name;
        public string Player1Name
        {
            get
            {
                return player1Name;
            }

            set
            {
                if (value != player1Name)
                {
                    player1Name = value;
                    StartGameCommand.RaiseCanExecuteChanged();
                }
            }
        }

        private string player2Name;
        public string Player2Name
        {
            get
            {
                return player2Name;
            }

            set
            {
                if (value != player2Name)
                {
                    player2Name = value;
                    StartGameCommand.RaiseCanExecuteChanged();
                }
            }
        }

        public RelayCommand StartGameCommand { get; }

        public RelayCommand BackCommand { get; }

        public NewGameViewModel()
        {
            MapTypes = new ObservableCollection<MapStrategy>
            {
                new DemoMap(),
                new SmallMap(),
                new StandardMap()
            };

            StartGameCommand = new RelayCommand(() =>
            {
                Messenger.Default.Send(new NavigationViewMessage(new GameViewModel()));

                Messenger.Default.Send(new GameMessage(
                    NewGameBuilder.Create()
                    .SetMap(selectedMapType)
                    .SetPlayerName(player1Name)
                    .SetPlayerName(player2Name)
                    .Build() , false
                ));
            }, () =>
            {
                return selectedMapType != null && !string.IsNullOrWhiteSpace(player1Name) && !string.IsNullOrWhiteSpace(player2Name);
            });

            BackCommand = new RelayCommand(() =>
            {
                Messenger.Default.Send(new NavigationSubviewMessage(new MainMenuViewModel()));
            });
        }
    }
}
