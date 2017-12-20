using GalaSoft.MvvmLight.Messaging;

namespace INSAWorld.ViewModel
{
    public class GameMessage : MessageBase
    {
        public Game Game { get; }

        public bool IsReplay { get; }

        public GameMessage(Game game, bool isReplay)
        {
            Game = game;
            IsReplay = isReplay;
        }
    }
}
