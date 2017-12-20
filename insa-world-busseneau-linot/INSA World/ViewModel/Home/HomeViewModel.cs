using GalaSoft.MvvmLight;
using GalaSoft.MvvmLight.Messaging;

namespace INSAWorld.ViewModel
{
    public class HomeViewModel : ViewModelBase
    {
        public ViewModelBase CurrentSubview { get; private set; }

        public HomeViewModel()
        {
            CurrentSubview = new MainMenuViewModel();

            Messenger.Default.Register<NavigationSubviewMessage>(this, message =>
            {
                CurrentSubview = message.Subview;
                RaisePropertyChanged("CurrentSubview");
            });
        }
    }
}
