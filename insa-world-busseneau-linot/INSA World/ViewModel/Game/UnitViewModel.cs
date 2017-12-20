using GalaSoft.MvvmLight;

namespace INSAWorld.ViewModel
{
    public class UnitViewModel : ViewModelBase
    {
        public Unit Unit { get; }

        public string WarriorAvatar
        {
            get
            {
                return $"/Resources/{Unit.GetType().Name}.png";
            }
        }

        public UnitViewModel(Unit unit)
        {
            Unit = unit;
        }
    }
}
