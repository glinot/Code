using GalaSoft.MvvmLight;
using GalaSoft.MvvmLight.Messaging;

namespace INSAWorld.ViewModel
{
    public class NavigationViewMessage : MessageBase
    {
        public ViewModelBase View { get; }

        public NavigationViewMessage(ViewModelBase view)
        {
            View = view;
        }
    }
}
