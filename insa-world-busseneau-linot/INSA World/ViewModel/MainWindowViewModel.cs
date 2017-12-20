using GalaSoft.MvvmLight;
using GalaSoft.MvvmLight.Messaging;

namespace INSAWorld.ViewModel
{
    public class MainWindowViewModel : ViewModelBase
    {
        public ViewModelBase View { get; private set; }

        public MainWindowViewModel()
        {
            View = new HomeViewModel();
            //View = new GameViewModel();

            Messenger.Default.Register<NavigationViewMessage>(this, message =>
            {
                View = message.View;
                RaisePropertyChanged("View");
            });
        }
    }
}
