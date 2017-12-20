using GalaSoft.MvvmLight.Messaging;

namespace INSAWorld.ViewModel
{
    public class CurrentPlayerMessage : MessageBase
    {
        public Player CurrentPlayer { get; }

        public CurrentPlayerMessage(Player currentPlayer)
        {
            CurrentPlayer = currentPlayer;
        }
    }
}
