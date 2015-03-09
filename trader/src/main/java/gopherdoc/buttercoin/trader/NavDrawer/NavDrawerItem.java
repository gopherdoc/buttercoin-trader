package gopherdoc.buttercoin.trader.NavDrawer;

public class NavDrawerItem {
    private String title;
    private boolean selected;
    public NavDrawerItem(){}
    public NavDrawerItem(String title){
        this.title = title;
    }
    public String getTitle(){
        return this.title;
    }

    public boolean isSelected(){
        return selected;
    }
    public void setSelected(boolean selected){
        this.selected = selected;
    }
}
