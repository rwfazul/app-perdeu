package br.com.perdeu;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.facebook.login.widget.ProfilePictureView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;

import br.com.perdeu.fragments.InicioFragment;
import br.com.perdeu.fragments.MeusAchadosFragment;
import br.com.perdeu.fragments.MeusPerdidosFragment;
import br.com.perdeu.fragments.NovoAchadoFragment;
import br.com.perdeu.fragments.NovoPerdidoFragment;
import br.com.perdeu.fragments.TodosAchadosFragment;
import br.com.perdeu.fragments.TodosPerdidosFragment;

public class DashboardActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth;
    FirebaseUser currentUser;
    private boolean viewIsAtHome;
    private FloatingActionButton fab;
    private Integer fabRedirect;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigationView.setCheckedItem(fabRedirect);
                if (fabRedirect != null)
                    displayView(fabRedirect);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        chooseFragment();
    }

    private void chooseFragment() {
        String redirect = getIntent().getStringExtra("fragment");
        Integer fragmentId = R.id.nav_inicio;
        if ( redirect != null ) {
            switch (redirect) {
                case "meus_achados" :
                    fragmentId = R.id.nav_meus_achados;
                    navigationView.setCheckedItem(R.id.nav_meus_achados);
                    break;
                case "meus_perdidos" :
                    fragmentId = R.id.nav_meus_perdidos;
                    navigationView.setCheckedItem(R.id.nav_meus_perdidos);
                    break;
            }
        }
        displayView(fragmentId);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        if (currentUser == null)
            updateUI();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        if (!viewIsAtHome) {
            Integer id = R.id.nav_inicio;
            String redirect = getIntent().getStringExtra("fragment");
            if ( redirect != null ) {
                switch (redirect) {
                    case "meus_achados":
                        id = R.id.nav_meus_achados;
                        break;
                    case "meus_perdidos":
                        id = R.id.nav_meus_perdidos;
                        break;
                }
            }
            navigationView.setCheckedItem(id);
            displayView(id);
        } else
            super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        TextView nomeUsuario = findViewById(R.id.nome_usuario);
        TextView emailUsuario = findViewById(R.id.email_usuario);
        ProfilePictureView profilePictureView = (ProfilePictureView) findViewById(R.id.imagem_usuario);

        nomeUsuario.setText(currentUser.getDisplayName());
        emailUsuario.setText(currentUser.getEmail());
        for (UserInfo profile : currentUser.getProviderData())
            profilePictureView.setProfileId(profile.getUid());
        getMenuInflater().inflate(R.menu.dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // logout
        if (id == R.id.realizar_logout) {
            mAuth.signOut(); // firebase
            LoginManager.getInstance().logOut(); // facebook
            updateUI();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        displayView(item.getItemId());

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void displayView(int nav_id) {
        Fragment fragment = null;
        String title = "";
        viewIsAtHome = false;
        fab.hide();
        switch (nav_id) {
            case R.id.nav_meus_achados:
                fragment = new MeusAchadosFragment();
                title = "Meus Achados";
                fabRedirect = R.id.nav_novo_achado;
                fab.show();
                break;
            case R.id.nav_meus_perdidos:
                fragment = new MeusPerdidosFragment();
                title = "Meus Perdidos";
                fabRedirect = R.id.nav_novo_perdido;
                fab.show();
                break;
            case R.id.nav_novo_achado:
                fragment = new NovoAchadoFragment();
                title = "Adicionar Achado";
                break;
            case R.id.nav_novo_perdido:
                fragment = new NovoPerdidoFragment();
                title = "Adicionar Perdido";
                break;
            case R.id.nav_todos_achados:
                fragment = new TodosAchadosFragment();
                title = "Todos Achados";
                break;
            case R.id.nav_todos_perdidos:
                fragment = new TodosPerdidosFragment();
                title = "Todos Perdidos";
                break;
            default:
                fragment = new InicioFragment();;
                title = "Perdeu?";
                viewIsAtHome = true;
        }

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, fragment).commit();
        getSupportActionBar().setTitle(title);
    }

    private void updateUI() {
        Toast.makeText(DashboardActivity.this, "Logout realizado com sucesso.", Toast.LENGTH_LONG).show();
        Intent accountIntent = new Intent(DashboardActivity.this, MainActivity.class);
        startActivity(accountIntent);
        finish();
    }

    public void showAndSetFab(String fabLink) {
        Integer id = null;
        if ( fabLink.equals("novo_achado") )
            id = R.id.nav_novo_achado;
        else if ( fabLink.equals("novo_perdido") )
            id = R.id.nav_novo_perdido;
        if (id != null) {
            fabRedirect = id;
            fab.show();
        }
    }

}