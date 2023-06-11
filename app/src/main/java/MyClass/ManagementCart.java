//package MyClass;
//
//import android.content.Context;
//import android.widget.Toast;
//
//
//
//import Interface.ChangNumberItemsListener;
//import Model.ProductModel;
//import MyClass.ClothingDomain;
//
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class ManagementCart {
//    private Context context;
//    private TinyDB tinyDB;
//
//    public ManagementCart(Context context) {
//        this.context = context;
//        this.tinyDB = new TinyDB(context);
//    }
//
//    public void insertVariantProduct(ProductModel item){
//        List<ProductModel> listFood = getListCart();
//        boolean existAlready = false;
//        int n = 0;
//        for (int i = 0; i < listFood.size(); i++){
//            if (listFood.get(i).getName().equals(item.getName())){
//                existAlready = true;
//                n = i;
//                break;
//            }
//        }
//
//        if (existAlready){
//            listFood.get(n).setNumberInCart(item.getNumberInCart());
//        } else {
//            listFood.add(item);
//        }
//
//        tinyDB.putListObject("CartList", listFood);
//        Toast.makeText(context, "Added To Your Cart", Toast.LENGTH_SHORT).show();
//    }
//
//    public List<ProductModel> getListCart(){
//        return tinyDB.getListObject("CartList");
//    }
//
//    public void plusNumberVariantProduct(List<ProductModel> listFood, int position, ChangNumberItemsListener changNumberItemsListener){
//        listFood.get(position).setNumberInCart(listFood.get(position).getNumberInCart() + 1);
//        tinyDB.putListObject("CartList", listFood);
//        changNumberItemsListener.changed();
//    }
//
//    public void minusNumberVariantProduct(List<ProductModel> listfood, int position, ChangNumberItemsListener changNumberItemsListener){
//        if (listfood.get(position).getNumberInCart() == 1){
//            listfood.remove(position);
//        } else {
//            listfood.get(position).setNumberInCart(listfood.get(position).getNumberInCart() - 1);
//        }
//
//        tinyDB.putListObject("CartList", listfood);
//        changNumberItemsListener.changed();
//    }
//
//    public Double getTotalFee() {
//        List<ProductModel> listfood = getListCart();
//        double fee = 0;
//        for (int i = 0; i < listfood.size(); i++){
//            fee = fee + (listfood.get(i).getPrice() * listfood.get(i).getNumberInCart());
//
//        }
//        return fee;
//    }
//}
