package com.example.valentin.conectare;

/**
 * Created by Valentin on 16-Jul-16.
 */
    import android.content.Context;
    import android.util.Log;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.BaseAdapter;
    import android.widget.ImageButton;
    import android.widget.TextView;
    import android.widget.Toast;

import java.util.ArrayList;

    public class MyCustomBaseAdapter extends BaseAdapter {
    private static ArrayList<SearchResults> searchArrayList;
    private LayoutInflater mInflater;
    ArrayList<String> list;
    Context context;
    String TAG = "adapter";
    public static int suma=0;
    public static int nrproduse=0;
    public static int comanda[] = new int[10];

    public MyCustomBaseAdapter(Context context, ArrayList<SearchResults> results) {
        searchArrayList = results;
        mInflater = LayoutInflater.from(context);
        this.context = context;
        this.list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            this.list.add("0");
            comanda[i]=0;
        }
        nrproduse=0;
        suma=0;
    }

    public int getCount() {
        return searchArrayList.size();
    }

    public Object getItem(int position) {
        return searchArrayList.get(position).getName();
    }
    public static Object getItemName(int position) {
        return searchArrayList.get(position).getName();
    }
    public static String getItemPret(int position) {
        String pret=searchArrayList.get(position).getPret();
        String[] a=pret.split(" ");
        return a[1];
    }


        public long getItemId(int position) {
        return position;
    }

    static class ViewHolder {
        TextView txtNumeProdus;
        TextView txtPret;
        TextView txtIngrediente;
        ImageButton imageButtonMinus;
        ImageButton imageButtonPlus;
        TextView txtTotal;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder=null;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item, null);
            holder = new ViewHolder();
            holder.txtNumeProdus = (TextView) convertView.findViewById(R.id.NumeProdus);
            holder.txtPret = (TextView) convertView.findViewById(R.id.pret);
            holder.txtIngrediente = (TextView) convertView.findViewById(R.id.ingrediente);
            holder.txtTotal = (TextView) convertView.findViewById(R.id.cart_product_quantity_tv);
            holder.imageButtonMinus = (ImageButton) convertView.findViewById(R.id.cart_minus_img);
            holder.imageButtonPlus = (ImageButton) convertView.findViewById(R.id.cart_plus_img);

            final View finalview = convertView;



            convertView.setTag(holder);
            finalview.setTag(holder);
            convertView.setTag(R.id.cart_minus_img, holder.imageButtonMinus);
            convertView.setTag(R.id.cart_plus_img, holder.imageButtonPlus);


        } else {
            holder = (ViewHolder) convertView.getTag();

        }

        holder.txtTotal.setText(list.get(position).toString());
        holder.imageButtonPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count = Integer.parseInt(list.get(position));
                count = count + 1;
                nrproduse++;
                suma=suma+Integer.parseInt(getItemPret(position));
               // Toast.makeText(context, String.valueOf(getItem(position)), Toast.LENGTH_SHORT).show();
                comanda[position]++;
                Log.d("comanda", String.valueOf(comanda[position]));
                Log.e(TAG, "onClick: " + count);
                list.set(position, String.valueOf(count));  //update your list like this
                notifyDataSetChanged();
            }
        });

        holder.imageButtonMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count = Integer.parseInt(list.get(position));
                if(comanda[position]>0)
                    comanda[position]--;
                if (count > 0)
                {
                    count = count - 1;
                    nrproduse--;
                    suma=suma-Integer.parseInt(getItemPret(position));

                    list.set(position, String.valueOf(count)); //update your list like this
                }
                else
                   // Toast.makeText(context, "not allowed", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "onClick: " + count);
                notifyDataSetChanged();
            }
        });

        holder.imageButtonMinus.setTag(position); // This line is important.
        holder.imageButtonPlus.setTag(position);

        holder.txtNumeProdus.setText(searchArrayList.get(position).getName());
        holder.txtPret.setText(searchArrayList.get(position)
                .getPret());
        holder.txtIngrediente.setText(searchArrayList.get(position).getIngrediente());

        return convertView;
    }

        public static int getNrproduse() {
            return nrproduse;
        }
        public static int getSuma() {
            return suma;
        }
        public static StringBuilder getComanda()
        {
            StringBuilder Comanda=  new StringBuilder();
            Comanda.setLength(0);
            for(int i=0; i<10; i++)
                if(comanda[i]>0)
                {
                    Comanda.append(String.valueOf(comanda[i]));
                    Comanda.append(" X ");
                    Comanda.append(getItemName(i));
                    Comanda.append(" ");
                    Comanda.append("Pret: ");
                    int pret=(comanda[i])*Integer.parseInt(getItemPret(i));
                    Comanda.append(String.valueOf(pret));
                    Comanda.append("ENDOFLINE ");
                }

            return Comanda;
        }
}