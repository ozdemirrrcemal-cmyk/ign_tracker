package com.guneymarmara.kampanyatakip;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.guneymarmara.kampanyatakip.data.AppUser;
import com.guneymarmara.kampanyatakip.data.Campaign;
import com.guneymarmara.kampanyatakip.data.CampaignProgress;
import com.guneymarmara.kampanyatakip.data.CampaignProgressStatus;
import com.guneymarmara.kampanyatakip.data.CampaignRepository;
import com.guneymarmara.kampanyatakip.data.CampaignTask;
import com.guneymarmara.kampanyatakip.data.SessionManager;
import com.guneymarmara.kampanyatakip.data.UserRole;

import java.util.List;

public class MainActivity extends Activity {

    private static final int GREEN = Color.rgb(15, 123, 63);
    private static final int GREEN_DARK = Color.rgb(7, 88, 43);
    private static final int YELLOW = Color.rgb(255, 201, 40);
    private static final int BACKGROUND = Color.rgb(244, 246, 245);
    private static final int TEXT = Color.rgb(20, 31, 26);
    private static final int MUTED = Color.rgb(100, 116, 139);
    private FrameLayout screenRoot;
    private String currentScreen = "login";
    private Campaign openedCampaign;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(GREEN_DARK);
        screenRoot = new FrameLayout(this);
        setContentView(screenRoot);
        if (SessionManager.getCurrentUser() == null) {
            renderLogin();
        } else {
            renderHome();
        }
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }

    private void setScreen(View view) {
        screenRoot.removeAllViews();
        screenRoot.addView(view, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
    }

    private TextView text(String value, int sp, int color, int style) {
        TextView textView = new TextView(this);
        textView.setText(value);
        textView.setTextSize(sp);
        textView.setTextColor(color);
        textView.setTypeface(Typeface.DEFAULT, style);
        textView.setIncludeFontPadding(true);
        textView.setLineSpacing(dp(2), 1.0f);
        return textView;
    }

    private Button button(String label) {
        Button button = new Button(this);
        button.setText(label);
        button.setAllCaps(false);
        button.setTextColor(Color.WHITE);
        button.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        button.setBackground(rounded(GREEN, dp(14), 0, 0));
        button.setMinHeight(dp(48));
        return button;
    }

    private Button outlineButton(String label) {
        Button button = new Button(this);
        button.setText(label);
        button.setAllCaps(false);
        button.setTextColor(GREEN_DARK);
        button.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        button.setBackground(rounded(Color.WHITE, dp(14), GREEN, dp(1)));
        button.setMinHeight(dp(48));
        return button;
    }

    private GradientDrawable rounded(int color, int radius, int strokeColor, int strokeWidth) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(radius);
        if (strokeWidth > 0) drawable.setStroke(strokeWidth, strokeColor);
        return drawable;
    }

    private GradientDrawable gradient(int startColor, int endColor) {
        GradientDrawable drawable = new GradientDrawable(GradientDrawable.Orientation.TL_BR, new int[]{startColor, endColor});
        drawable.setCornerRadius(dp(24));
        return drawable;
    }

    private LinearLayout card() {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(16), dp(16), dp(16), dp(16));
        card.setBackground(rounded(Color.WHITE, dp(18), Color.rgb(226, 232, 240), dp(1)));
        card.setElevation(dp(2));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, dp(12));
        card.setLayoutParams(params);
        return card;
    }

    private TextView chip(String label, int backgroundColor, int textColor) {
        TextView chip = text(label, 12, textColor, Typeface.BOLD);
        chip.setGravity(Gravity.CENTER);
        chip.setPadding(dp(10), dp(5), dp(10), dp(5));
        chip.setBackground(rounded(backgroundColor, dp(999), 0, 0));
        return chip;
    }

    private LinearLayout verticalRoot() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(BACKGROUND);
        return root;
    }

    private LinearLayout contentWithToolbar(String title, boolean back) {
        LinearLayout root = verticalRoot();
        root.addView(toolbar(title, back));
        ScrollView scrollView = new ScrollView(this);
        scrollView.setFillViewport(false);
        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(dp(18), dp(18), dp(18), dp(28));
        scrollView.addView(content, new ScrollView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        root.addView(scrollView, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                1f
        ));
        content.setTag(root);
        return content;
    }

    private View toolbar(String title, boolean back) {
        LinearLayout bar = new LinearLayout(this);
        bar.setOrientation(LinearLayout.HORIZONTAL);
        bar.setGravity(Gravity.CENTER_VERTICAL);
        bar.setPadding(dp(12), dp(12), dp(12), dp(12));
        bar.setBackgroundColor(GREEN_DARK);

        if (back) {
            TextView backButton = text("‹", 34, Color.WHITE, Typeface.BOLD);
            backButton.setGravity(Gravity.CENTER);
            bar.addView(backButton, new LinearLayout.LayoutParams(dp(44), dp(44)));
            backButton.setOnClickListener(v -> goBack());
        }

        LinearLayout titleBox = new LinearLayout(this);
        titleBox.setOrientation(LinearLayout.VERTICAL);
        titleBox.setPadding(back ? dp(4) : 0, 0, 0, 0);
        TextView titleView = text(title, 18, Color.WHITE, Typeface.BOLD);
        titleBox.addView(titleView);
        AppUser user = SessionManager.getCurrentUser();
        if (user != null) {
            TextView sub = text(user.fullName + " • " + user.role.displayName(), 12, Color.rgb(224, 242, 232), Typeface.NORMAL);
            titleBox.addView(sub);
        }
        bar.addView(titleBox, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        if (user != null) {
            TextView logout = text("Çıkış", 13, Color.WHITE, Typeface.BOLD);
            logout.setPadding(dp(12), dp(8), dp(12), dp(8));
            logout.setBackground(rounded(Color.argb(35, 255, 255, 255), dp(999), Color.argb(80, 255, 255, 255), dp(1)));
            logout.setOnClickListener(v -> {
                SessionManager.logout();
                renderLogin();
            });
            bar.addView(logout);
        }
        return bar;
    }

    private void renderLogin() {
        currentScreen = "login";
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(20), dp(28), dp(20), dp(28));
        root.setBackgroundColor(BACKGROUND);
        scroll.addView(root, new ScrollView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        LinearLayout hero = new LinearLayout(this);
        hero.setOrientation(LinearLayout.VERTICAL);
        hero.setPadding(dp(22), dp(24), dp(22), dp(24));
        hero.setBackground(gradient(GREEN_DARK, GREEN));
        hero.setElevation(dp(4));
        root.addView(hero, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        hero.addView(text("Güney Marmara", 17, Color.rgb(232, 245, 238), Typeface.BOLD));
        hero.addView(text("Kampanya Takip", 31, Color.WHITE, Typeface.BOLD));
        hero.addView(text("Şube kampanyaları, görev onayları ve yönetici takibi için operasyon uygulaması.", 14, Color.rgb(232, 245, 238), Typeface.NORMAL));

        addSpace(root, 18);
        LinearLayout loginCard = card();
        root.addView(loginCard);
        loginCard.addView(text("Test Girişi", 22, TEXT, Typeface.BOLD));
        loginCard.addView(text("Şimdilik backend yok. Aşağıdaki test hesapları local olarak çalışır.", 14, MUTED, Typeface.NORMAL));
        addSpace(loginCard, 10);

        EditText email = input("E-posta");
        email.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        loginCard.addView(email);
        addSpace(loginCard, 10);
        EditText password = input("Şifre");
        password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        loginCard.addView(password);
        addSpace(loginCard, 12);

        Button login = button("Giriş Yap");
        loginCard.addView(login);
        login.setOnClickListener(v -> {
            AppUser user = SessionManager.login(email.getText().toString(), password.getText().toString());
            if (user == null) {
                Toast.makeText(this, "E-posta veya şifre hatalı", Toast.LENGTH_SHORT).show();
            } else {
                renderHome();
            }
        });

        addSpace(loginCard, 12);
        loginCard.addView(text("Hızlı test hesapları", 15, TEXT, Typeface.BOLD));
        addTestLogin(loginCard, "Admin", "admin@gm-kampanya.test", "Bölge yönetimi / tüm panel erişimi", email, password);
        addTestLogin(loginCard, "Mağaza Yöneticisi", "mudur@gm-kampanya.test", "Şube yönetici görünümü", email, password);
        addTestLogin(loginCard, "Çalışan", "calisan@gm-kampanya.test", "Görev işaretleme ve kampanya onayı", email, password);
        setScreen(scroll);
    }

    private EditText input(String hint) {
        EditText input = new EditText(this);
        input.setHint(hint);
        input.setSingleLine(true);
        input.setTextColor(TEXT);
        input.setHintTextColor(Color.rgb(148, 163, 184));
        input.setPadding(dp(14), 0, dp(14), 0);
        input.setBackground(rounded(Color.rgb(248, 250, 252), dp(12), Color.rgb(203, 213, 225), dp(1)));
        input.setMinHeight(dp(52));
        input.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        return input;
    }

    private void addTestLogin(LinearLayout parent, String role, String mail, String info, EditText email, EditText password) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.VERTICAL);
        row.setPadding(dp(12), dp(10), dp(12), dp(10));
        row.setBackground(rounded(Color.rgb(248, 250, 252), dp(14), Color.rgb(226, 232, 240), dp(1)));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, dp(8), 0, 0);
        parent.addView(row, params);
        row.addView(text(role + " • " + mail, 14, TEXT, Typeface.BOLD));
        row.addView(text(info + " • Şifre: 123456", 12, MUTED, Typeface.NORMAL));
        row.setOnClickListener(v -> {
            email.setText(mail);
            password.setText("123456");
        });
    }

    private void renderHome() {
        currentScreen = "home";
        AppUser user = SessionManager.getCurrentUser();
        LinearLayout content = contentWithToolbar("Kampanya Takip", false);
        LinearLayout root = (LinearLayout) content.getTag();

        LinearLayout hero = new LinearLayout(this);
        hero.setOrientation(LinearLayout.VERTICAL);
        hero.setPadding(dp(18), dp(18), dp(18), dp(18));
        hero.setBackground(gradient(GREEN_DARK, GREEN));
        hero.setElevation(dp(3));
        content.addView(hero, fullWidthBottom(14));
        hero.addView(text("Merhaba, " + user.fullName, 24, Color.WHITE, Typeface.BOLD));
        hero.addView(text(user.storeName, 13, Color.rgb(232, 245, 238), Typeface.NORMAL));
        addSpace(hero, 10);
        hero.addView(chip(user.role.displayName(), YELLOW, Color.rgb(59, 43, 0)));

        LinearLayout stats = new LinearLayout(this);
        stats.setOrientation(LinearLayout.HORIZONTAL);
        content.addView(stats, fullWidthBottom(12));
        addStat(stats, "Aktif", String.valueOf(activeCampaignCount()), "kampanya");
        addStat(stats, "Onay", employeeStatusSummary(user), user.role == UserRole.EMPLOYEE ? "durum" : "takip");

        LinearLayout actions = card();
        content.addView(actions);
        actions.addView(text("Hızlı işlemler", 19, TEXT, Typeface.BOLD));
        addSpace(actions, 8);
        Button campaigns = button("Kampanyaları Gör");
        actions.addView(campaigns);
        campaigns.setOnClickListener(v -> renderCampaigns());
        addSpace(actions, 8);
        if (user.isManagerOrAdmin()) {
            Button manager = outlineButton("Yönetici Paneli");
            actions.addView(manager);
            manager.setOnClickListener(v -> renderManagerDashboard());
            addSpace(actions, 8);
            Button create = outlineButton("Yeni Kampanya Oluştur");
            actions.addView(create);
            create.setOnClickListener(v -> renderCreateCampaign());
        }

        content.addView(text("Bugünkü kampanya kartları", 19, TEXT, Typeface.BOLD));
        addSpace(content, 8);
        for (Campaign campaign : CampaignRepository.getCampaigns()) {
            content.addView(campaignCard(campaign));
        }
        setScreen(root);
    }

    private int activeCampaignCount() {
        int count = 0;
        for (Campaign campaign : CampaignRepository.getCampaigns()) {
            switch (campaign.status) {
                case ACTIVE:
                case ENDING_SOON:
                    count++;
                    break;
                default:
                    break;
            }
        }
        return count;
    }

    private String employeeStatusSummary(AppUser user) {
        if (user.role != UserRole.EMPLOYEE) return "Panel";
        int approved = 0;
        int total = 0;
        for (Campaign campaign : CampaignRepository.getCampaigns()) {
            CampaignProgress progress = CampaignRepository.getProgressForUser(campaign, user);
            total++;
            if (progress.status == CampaignProgressStatus.APPROVED) approved++;
        }
        return approved + "/" + total;
    }

    private void addStat(LinearLayout parent, String title, String value, String subtitle) {
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setPadding(dp(14), dp(14), dp(14), dp(14));
        box.setBackground(rounded(Color.WHITE, dp(18), Color.rgb(226, 232, 240), dp(1)));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        params.setMargins(0, 0, dp(8), 0);
        parent.addView(box, params);
        box.addView(text(title, 13, MUTED, Typeface.BOLD));
        box.addView(text(value, 26, GREEN_DARK, Typeface.BOLD));
        box.addView(text(subtitle, 12, MUTED, Typeface.NORMAL));
    }

    private void renderCampaigns() {
        currentScreen = "campaigns";
        LinearLayout content = contentWithToolbar("Kampanyalar", true);
        LinearLayout root = (LinearLayout) content.getTag();
        content.addView(text("Kampanya listesi", 24, TEXT, Typeface.BOLD));
        content.addView(text("Çalışan kampanyaya girip görevleri tamamlar. Yönetici durumları panelden takip eder.", 14, MUTED, Typeface.NORMAL));
        addSpace(content, 12);
        for (Campaign campaign : CampaignRepository.getCampaigns()) {
            content.addView(campaignCard(campaign));
        }
        setScreen(root);
    }

    private LinearLayout campaignCard(Campaign campaign) {
        LinearLayout card = card();
        LinearLayout visual = new LinearLayout(this);
        visual.setOrientation(LinearLayout.VERTICAL);
        visual.setGravity(Gravity.BOTTOM | Gravity.START);
        visual.setPadding(dp(16), dp(16), dp(16), dp(16));
        visual.setBackground(gradient(GREEN, GREEN_DARK));
        card.addView(visual, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(132)
        ));
        visual.addView(text(campaign.visualTitle, 22, Color.WHITE, Typeface.BOLD));
        visual.addView(text(campaign.discountText, 14, YELLOW, Typeface.BOLD));

        addSpace(card, 12);
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.addView(chip(campaign.status.displayName(), Color.rgb(220, 252, 231), GREEN_DARK));
        addSpaceHorizontal(row, 8);
        row.addView(chip(campaign.priority, Color.rgb(254, 243, 199), Color.rgb(146, 64, 14)));
        card.addView(row);
        addSpace(card, 8);
        card.addView(text(campaign.title, 19, TEXT, Typeface.BOLD));
        card.addView(text(campaign.description, 14, MUTED, Typeface.NORMAL));
        addSpace(card, 8);
        card.addView(text("Başlangıç: " + campaign.startDate + "  •  Bitiş: " + campaign.endDate, 12, MUTED, Typeface.NORMAL));
        card.addView(text("Hedef: " + campaign.targetGroup, 12, MUTED, Typeface.NORMAL));
        addSpace(card, 12);
        Button details = button("Detayları Aç");
        card.addView(details);
        details.setOnClickListener(v -> renderCampaignDetail(campaign));
        return card;
    }

    private void renderCampaignDetail(Campaign campaign) {
        currentScreen = "detail";
        openedCampaign = campaign;
        AppUser user = SessionManager.getCurrentUser();
        LinearLayout content = contentWithToolbar("Kampanya Detayı", true);
        LinearLayout root = (LinearLayout) content.getTag();

        LinearLayout visual = new LinearLayout(this);
        visual.setOrientation(LinearLayout.VERTICAL);
        visual.setGravity(Gravity.BOTTOM | Gravity.START);
        visual.setPadding(dp(18), dp(18), dp(18), dp(18));
        visual.setBackground(gradient(GREEN_DARK, GREEN));
        content.addView(visual, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(180)
        ));
        visual.addView(text(campaign.visualTitle, 27, Color.WHITE, Typeface.BOLD));
        visual.addView(text(campaign.discountText, 15, YELLOW, Typeface.BOLD));
        addSpace(content, 14);

        LinearLayout detail = card();
        content.addView(detail);
        detail.addView(text(campaign.title, 22, TEXT, Typeface.BOLD));
        detail.addView(text(campaign.description, 14, MUTED, Typeface.NORMAL));
        addSpace(detail, 8);
        detail.addView(text("Başlangıç: " + campaign.startDate, 13, TEXT, Typeface.BOLD));
        detail.addView(text("Bitiş: " + campaign.endDate, 13, TEXT, Typeface.BOLD));
        detail.addView(text("Hedef: " + campaign.targetGroup, 13, TEXT, Typeface.BOLD));

        if (user.role == UserRole.EMPLOYEE) {
            CampaignProgress progress = CampaignRepository.getProgressForUser(campaign, user);
            progress.markSeen();
            renderEmployeeApproval(content, campaign, progress);
        } else {
            renderManagerCampaignInfo(content, campaign);
        }
        setScreen(root);
    }

    private void renderEmployeeApproval(LinearLayout content, Campaign campaign, CampaignProgress progress) {
        LinearLayout statusCard = card();
        content.addView(statusCard);
        statusCard.addView(text("Çalışan onay durumu", 18, TEXT, Typeface.BOLD));
        statusCard.addView(text("Durum: " + progress.status.displayName(), 15, GREEN_DARK, Typeface.BOLD));
        statusCard.addView(text("Tamamlanan zorunlu görev: " + progress.completedRequiredCount(campaign) + "/" + campaign.requiredTaskCount(), 13, MUTED, Typeface.NORMAL));

        LinearLayout taskCard = card();
        content.addView(taskCard);
        taskCard.addView(text("Görev kontrol listesi", 18, TEXT, Typeface.BOLD));
        taskCard.addView(text("Tüm zorunlu görevler tamamlanmadan kampanya onayı verilemez.", 13, MUTED, Typeface.NORMAL));
        addSpace(taskCard, 8);

        for (CampaignTask task : campaign.tasks) {
            LinearLayout taskRow = new LinearLayout(this);
            taskRow.setOrientation(LinearLayout.VERTICAL);
            taskRow.setPadding(dp(10), dp(8), dp(10), dp(8));
            taskRow.setBackground(rounded(Color.rgb(248, 250, 252), dp(12), Color.rgb(226, 232, 240), dp(1)));
            LinearLayout.LayoutParams taskParams = fullWidthBottom(8);
            taskCard.addView(taskRow, taskParams);

            CheckBox checkBox = new CheckBox(this);
            checkBox.setText(task.title + (task.required ? " *" : ""));
            checkBox.setTextSize(15);
            checkBox.setTextColor(TEXT);
            checkBox.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
            checkBox.setChecked(progress.isTaskCompleted(task.id));
            checkBox.setEnabled(progress.status != CampaignProgressStatus.APPROVED);
            taskRow.addView(checkBox);
            taskRow.addView(text(task.description, 12, MUTED, Typeface.NORMAL));
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                progress.setTaskCompleted(task.id, isChecked);
                renderCampaignDetail(campaign);
            });
        }

        Button approve = button(progress.status == CampaignProgressStatus.APPROVED ? "Kampanya Onaylandı" : "Mağazamda Aktif Olarak Onayla");
        approve.setEnabled(progress.status != CampaignProgressStatus.APPROVED && progress.allRequiredTasksDone(campaign));
        if (!approve.isEnabled()) {
            approve.setTextColor(Color.rgb(229, 231, 235));
            approve.setBackground(rounded(Color.rgb(148, 163, 184), dp(14), 0, 0));
        }
        taskCard.addView(approve);
        approve.setOnClickListener(v -> {
            progress.approve(campaign);
            Toast.makeText(this, "Kampanya şube için onaylandı", Toast.LENGTH_SHORT).show();
            renderCampaignDetail(campaign);
        });
    }

    private void renderManagerCampaignInfo(LinearLayout content, Campaign campaign) {
        LinearLayout managerCard = card();
        content.addView(managerCard);
        managerCard.addView(text("Yönetici görünümü", 18, TEXT, Typeface.BOLD));
        managerCard.addView(text("Bu ekranda kampanya detayını görürsün. Çalışan görevleri işaretler; sen takip panelinden hangi şubenin onay verdiğini kontrol edersin.", 14, MUTED, Typeface.NORMAL));
        addSpace(managerCard, 10);
        Button dashboard = button("Bu Kampanyanın Takip Durumunu Aç");
        managerCard.addView(dashboard);
        dashboard.setOnClickListener(v -> renderManagerDashboard(campaign.id));

        LinearLayout taskCard = card();
        content.addView(taskCard);
        taskCard.addView(text("Çalışana giden görevler", 18, TEXT, Typeface.BOLD));
        for (CampaignTask task : campaign.tasks) {
            taskCard.addView(text("• " + task.title + (task.required ? "  (zorunlu)" : ""), 14, TEXT, Typeface.BOLD));
            taskCard.addView(text(task.description, 12, MUTED, Typeface.NORMAL));
            addSpace(taskCard, 6);
        }
    }

    private void renderManagerDashboard() {
        renderManagerDashboard(-1L);
    }

    private void renderManagerDashboard(long onlyCampaignId) {
        currentScreen = "manager";
        AppUser user = SessionManager.getCurrentUser();
        if (user == null || !user.isManagerOrAdmin()) {
            Toast.makeText(this, "Bu ekran için yönetici yetkisi gerekir", Toast.LENGTH_SHORT).show();
            renderHome();
            return;
        }
        LinearLayout content = contentWithToolbar("Yönetici Paneli", true);
        LinearLayout root = (LinearLayout) content.getTag();
        content.addView(text("Kampanya uygulama takibi", 23, TEXT, Typeface.BOLD));
        content.addView(text("Kim gördü, kim görevleri tamamladı, hangi şube onay verdi buradan takip edilir.", 14, MUTED, Typeface.NORMAL));
        addSpace(content, 12);

        int waiting = 0;
        int progressCount = 0;
        int approved = 0;
        for (CampaignProgress progress : CampaignRepository.getAllProgress()) {
            if (onlyCampaignId != -1L && progress.campaignId != onlyCampaignId) continue;
            if (progress.status == CampaignProgressStatus.APPROVED) approved++;
            else if (progress.status == CampaignProgressStatus.IN_PROGRESS || progress.status == CampaignProgressStatus.SEEN) progressCount++;
            else waiting++;
        }
        LinearLayout statRow = new LinearLayout(this);
        statRow.setOrientation(LinearLayout.HORIZONTAL);
        content.addView(statRow, fullWidthBottom(12));
        addMiniStat(statRow, "Bekliyor", waiting);
        addMiniStat(statRow, "İşlemde", progressCount);
        addMiniStat(statRow, "Onay", approved);

        for (Campaign campaign : CampaignRepository.getCampaigns()) {
            if (onlyCampaignId != -1L && campaign.id != onlyCampaignId) continue;
            LinearLayout campaignBlock = card();
            content.addView(campaignBlock);
            campaignBlock.addView(text(campaign.title, 18, TEXT, Typeface.BOLD));
            campaignBlock.addView(text(campaign.startDate + " - " + campaign.endDate, 12, MUTED, Typeface.NORMAL));
            addSpace(campaignBlock, 10);
            List<CampaignProgress> progressList = CampaignRepository.getProgressForCampaign(campaign.id);
            for (CampaignProgress progress : progressList) {
                campaignBlock.addView(progressRow(campaign, progress));
            }
        }
        setScreen(root);
    }

    private void addMiniStat(LinearLayout parent, String title, int count) {
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setGravity(Gravity.CENTER);
        box.setPadding(dp(8), dp(12), dp(8), dp(12));
        box.setBackground(rounded(Color.WHITE, dp(16), Color.rgb(226, 232, 240), dp(1)));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        params.setMargins(0, 0, dp(8), 0);
        parent.addView(box, params);
        box.addView(text(String.valueOf(count), 24, GREEN_DARK, Typeface.BOLD));
        box.addView(text(title, 12, MUTED, Typeface.BOLD));
    }

    private View progressRow(Campaign campaign, CampaignProgress progress) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.VERTICAL);
        row.setPadding(dp(12), dp(10), dp(12), dp(10));
        row.setBackground(rounded(Color.rgb(248, 250, 252), dp(14), Color.rgb(226, 232, 240), dp(1)));
        row.setLayoutParams(fullWidthBottom(8));

        LinearLayout top = new LinearLayout(this);
        top.setOrientation(LinearLayout.HORIZONTAL);
        top.setGravity(Gravity.CENTER_VERTICAL);
        TextView name = text(progress.userName, 15, TEXT, Typeface.BOLD);
        top.addView(name, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        top.addView(statusChip(progress.status));
        row.addView(top);
        row.addView(text(progress.storeName, 12, MUTED, Typeface.NORMAL));
        row.addView(text("Görev: " + progress.completedRequiredCount(campaign) + "/" + campaign.requiredTaskCount()
                + " • Görüldü: " + (progress.seenAt == null ? "-" : progress.seenAt)
                + " • Onay: " + (progress.completedAt == null ? "-" : progress.completedAt), 12, MUTED, Typeface.NORMAL));
        return row;
    }

    private TextView statusChip(CampaignProgressStatus status) {
        switch (status) {
            case APPROVED:
                return chip("Onaylandı", Color.rgb(220, 252, 231), GREEN_DARK);
            case IN_PROGRESS:
                return chip("İşlemde", Color.rgb(254, 243, 199), Color.rgb(146, 64, 14));
            case SEEN:
                return chip("Görüldü", Color.rgb(219, 234, 254), Color.rgb(30, 64, 175));
            case OVERDUE:
                return chip("Gecikti", Color.rgb(254, 226, 226), Color.rgb(153, 27, 27));
            default:
                return chip("Bekliyor", Color.rgb(241, 245, 249), Color.rgb(71, 85, 105));
        }
    }

    private void renderCreateCampaign() {
        currentScreen = "create";
        AppUser user = SessionManager.getCurrentUser();
        if (user == null || !user.isManagerOrAdmin()) {
            Toast.makeText(this, "Kampanya oluşturmak için yönetici yetkisi gerekir", Toast.LENGTH_SHORT).show();
            renderHome();
            return;
        }
        LinearLayout content = contentWithToolbar("Kampanya Oluştur", true);
        LinearLayout root = (LinearLayout) content.getTag();
        content.addView(text("Yeni kampanya girişi", 24, TEXT, Typeface.BOLD));
        content.addView(text("Bu ekran MVP amaçlıdır. Görsel yükleme ve push bildirim sonraki aşamada backend ile bağlanacak.", 14, MUTED, Typeface.NORMAL));
        addSpace(content, 12);

        LinearLayout form = card();
        content.addView(form);
        EditText title = input("Kampanya adı");
        title.setText("Bölgesel Fırsat Alanı Kontrolü");
        form.addView(title);
        addSpace(form, 10);
        EditText visual = input("Görsel başlığı / kapak metni");
        visual.setText("Güney Marmara Fırsatları");
        form.addView(visual);
        addSpace(form, 10);
        EditText description = input("Açıklama");
        description.setSingleLine(false);
        description.setMinLines(3);
        description.setText("Şube kampanya alanı, fiyat etiketi ve raf düzeni kontrol edilecek.");
        form.addView(description);
        addSpace(form, 10);
        EditText start = input("Başlangıç tarih/saat");
        start.setText("15.06.2026 09:00");
        form.addView(start);
        addSpace(form, 10);
        EditText end = input("Bitiş tarih/saat");
        end.setText("22.06.2026 22:00");
        form.addView(end);
        addSpace(form, 10);
        EditText discount = input("Kampanya görsel alt metni");
        discount.setText("Haftalık mağaza uygulama kontrolü");
        form.addView(discount);
        addSpace(form, 10);
        EditText target = input("Hedef şube/personel");
        target.setText("Güney Marmara tüm şubeler");
        form.addView(target);
        addSpace(form, 12);
        Button save = button("Kampanyayı Kaydet");
        form.addView(save);
        save.setOnClickListener(v -> {
            if (title.getText().toString().trim().isEmpty()) {
                Toast.makeText(this, "Kampanya adı zorunlu", Toast.LENGTH_SHORT).show();
                return;
            }
            Campaign created = CampaignRepository.addCampaign(
                    title.getText().toString().trim(),
                    description.getText().toString().trim(),
                    visual.getText().toString().trim(),
                    start.getText().toString().trim(),
                    end.getText().toString().trim(),
                    "Normal",
                    discount.getText().toString().trim(),
                    target.getText().toString().trim()
            );
            Toast.makeText(this, "Kampanya oluşturuldu", Toast.LENGTH_SHORT).show();
            renderCampaignDetail(created);
        });
        setScreen(root);
    }

    private void goBack() {
        if ("detail".equals(currentScreen)) {
            renderCampaigns();
        } else if ("campaigns".equals(currentScreen) || "manager".equals(currentScreen) || "create".equals(currentScreen)) {
            renderHome();
        } else {
            renderHome();
        }
    }

    @Override
    public void onBackPressed() {
        if ("login".equals(currentScreen) || "home".equals(currentScreen)) {
            super.onBackPressed();
        } else {
            goBack();
        }
    }

    private void addSpace(LinearLayout parent, int heightDp) {
        View space = new View(this);
        parent.addView(space, new LinearLayout.LayoutParams(1, dp(heightDp)));
    }

    private void addSpaceHorizontal(LinearLayout parent, int widthDp) {
        View space = new View(this);
        parent.addView(space, new LinearLayout.LayoutParams(dp(widthDp), 1));
    }

    private LinearLayout.LayoutParams fullWidthBottom(int bottomDp) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, dp(bottomDp));
        return params;
    }
}
