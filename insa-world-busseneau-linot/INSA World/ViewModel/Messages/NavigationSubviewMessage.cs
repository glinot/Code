using GalaSoft.MvvmLight;
using GalaSoft.MvvmLight.Messaging;

namespace INSAWorld.ViewModel
{
    public class NavigationSubviewMessage : MessageBase
    {
        public ViewModelBase Subview { get; }

        public NavigationSubviewMessage(ViewModelBase subview)
        {
            Subview = subview;
        }
    }
}
