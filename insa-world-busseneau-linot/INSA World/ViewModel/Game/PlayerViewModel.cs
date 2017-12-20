using GalaSoft.MvvmLight;
using GalaSoft.MvvmLight.Messaging;
using System.Collections.Generic;
using System.Collections.ObjectModel;

namespace INSAWorld.ViewModel
{
    public class PlayerViewModel : ViewModelBase
    {
        public Player Player { get; }

        public ObservableCollection<UnitViewModel> Units { get; }

        public string RaceAvatar
        {
            get
            {
                return $"/Resources/{Player.Race.GetType().Name}.png";
            }
        }

        public bool IsYourTurn { get; private set; }

        public PlayerViewModel(Player player)
        {
            Player = player;

            Units = new ObservableCollection<UnitViewModel>();
            foreach (var unit in Player.Units)
                Units.Add(new UnitViewModel(unit));

            Messenger.Default.Register<CurrentPlayerMessage>(this, message =>
            {
                IsYourTurn = message.CurrentPlayer == Player;
                RaisePropertyChanged("IsYourTurn");
            });
        }
    }
}
