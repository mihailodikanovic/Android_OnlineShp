package mihailo.dikanovic.shoppinglist;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegisterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterFragment extends Fragment
{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    Button register;
    EditText username;
    EditText email;
    EditText password;
    CheckBox checkBox;

    String admin;
    String BASE_URL = "http://localhost:3000";
    boolean isAdmin;

    Intent intent;
    OnlineShopDB dbHelper;
    HttpHelper httpHelper;

    public RegisterFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RegisterFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RegisterFragment newInstance(String param1, String param2)
    {
        RegisterFragment fragment = new RegisterFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        dbHelper = new OnlineShopDB(getContext());
        httpHelper = new HttpHelper();

        register = view.findViewById(R.id.button_register_fragment);

        username = view.findViewById(R.id.register_username);
        email = view.findViewById(R.id.register_email);
        password = view.findViewById(R.id.register_password);
        checkBox = view.findViewById(R.id.checkBox);



        register.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                isAdmin = checkBox.isChecked();
                final String[] id = new String[1];
                new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        try {
                            JSONObject jsonObject = httpHelper.postRegisterUser(username.getText().toString(), email.getText().toString(), password.getText().toString(), isAdmin);
                            if (jsonObject == null)
                            {
                                try {
                                    getActivity().runOnUiThread(new Runnable()
                                    {
                                        public void run()
                                        {
                                            Toast.makeText(getActivity(), "Failed to register", Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                                catch (Exception e)
                                {
                                    e.printStackTrace();
                                }
                                Thread.currentThread().stop();
                            }

                            try
                            {
                                getActivity().runOnUiThread(new Runnable()
                                {
                                    public void run()
                                    {
                                        Toast.makeText(getActivity(), "Successfully registered.", Toast.LENGTH_LONG).show();
                                        try
                                        {
                                            id[0] = jsonObject.getString("_id");
                                        }
                                        catch (JSONException e)
                                        {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            }
                            catch (Exception e)
                            {
                                e.printStackTrace();
                            }

                            dbHelper.insertUser(new User(id[0], username.getText().toString(), email.getText().toString(), password.getText().toString(), isAdmin)); // Ažurirajte korisnika sa dodatnim informacijama iz odgovora

                            intent = new Intent(getActivity(), HomeActivity.class);

                            Bundle bundle = new Bundle();
                            bundle.putString("username", String.valueOf(username.getText()));
                            bundle.putString("password", String.valueOf(password.getText()));

                            intent.putExtras(bundle);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                }).start();

            }
        });

        return view;
    }

}