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
import android.widget.EditText;
import android.widget.FrameLayout;
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

    private static final int ORANGE = Color.rgb(246, 130, 31);
    private static final int ORANGE_DARK = Color.rgb(218, 96, 14);
    private static final int ORANGE_SOFT = Color.rgb(255, 244, 235);
    private static final int BACKGROUND = Color.rgb(248, 250, 252);
    private static final int CARD_BORDER = Color.rgb(226, 232, 240);
    private static final int TEXT = Color.rgb(31, 41, 55);
    private static final int MUTED = Color.rgb(100, 116, 139);
    private static final int SUCCESS = Color.rgb(22, 125, 70);
    private static final int WARNING = Color.rgb(180, 83, 9);

    private FrameLayout screenRoot;
    private String currentScreen = "login";
    private Campaign openedCampaign;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(ORANGE_DARK);
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
        button.setTextSize(14);
        button.setTextColor(Color.WHITE);
        button.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        button.setBackground(rounded(ORANGE, dp(12), 0, 0));
        button.setMinHeight(dp(44));
        button.setPadding(dp(12), 0, dp(12), 0);
        return button;
    }

    private Button outlineButton(String label) {
        Button button = new Button(this);
        button.setText(label);
        button.setAllCaps(false);
        button.setTextSize(14);
        button.setTextColor(ORANGE_DARK);
        button.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        button.setBackground(rounded(Color.WHITE, dp(12), ORANGE, dp(1)));
        button.setMinHeight(dp(44));
        button.setPadding(dp(12), 0, dp(12), 0);
        return button;
    }

    private Button disabledButton(String label) {
        Button button = button(label);
        button.setEnabled(false);
        button.setTextColor(Color.rgb(148, 163, 184));
        button.setBackground(rounded(Color.rgb(241, 245, 249), dp(12), CARD_BORDER, dp(1)));
        return button;
    }

    private GradientDrawable rounded(int color, int radius, int strokeColor, int strokeWidth) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(radius);
        if (strokeWidth > 0) {
            drawable.setStroke(strokeWidth, strokeColor);
        }
        return drawable;
    }

    private GradientDrawable gradient(int startColor, int endColor) {
        GradientDrawable drawable = new GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                new int[]{startColor, endColor}
        );
        drawable.setCornerRadius(dp(18));
        return drawable;
    }

    private LinearLayout card() {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(14), dp(14), dp(14), dp(14));
        card.setBackground(rounded(Color.WHITE, dp(16), CARD_BORDER, dp(1)));
        card.setElevation(dp(1));
        card.setLayoutParams(fullWidthBottom(10));
        return card;
    }

    private TextView chip(String label, int backgroundColor, int textColor) {
        TextView chip = text(label, 11, textColor, Typeface.BOLD);
        chip.setGravity(Gravity.CENTER);
        chip.setPadding(dp(9), dp(4), dp(9), dp(4));
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
        content.setPadding(dp(14), dp(14), dp(14), dp(22));

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
        bar.setPadding(dp(10), dp(8), dp(10), dp(8));
        bar.setBackgroundColor(Color.WHITE);
        bar.setElevation(dp(3));

        if (back) {
            TextView backButton = text("‹", 30, ORANGE_DARK, Typeface.BOLD);
            backButton.setGravity(Gravity.CENTER);
            bar.addView(backButton, new LinearLayout.LayoutParams(dp(38), dp(38)));
            backButton.setOnClickListener(v -> goBack());
        }

        LinearLayout titleBox = new LinearLayout(this);
        titleBox.setOrientation(LinearLayout.VERTICAL);
        titleBox.setPadding(back ? dp(4) : 0, 0, 0, 0);

        TextView titleView = text(title, 17, TEXT, Typeface.BOLD);
        titleBox.addView(titleView);

        AppUser user = SessionManager.getCurrentUser();
        if (user != null) {
            String subtitle = user.role.displayName();
            if (user.storeCode != null && user.storeCode.length() > 0 && !"ADMIN".equals(user.storeCode)) {
                subtitle = user.storeCode + " • " + user.storeName;
            }
            titleBox.addView(text(subtitle, 11, MUTED, Typeface.NORMAL));
        }

        bar.addView(titleBox, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        if (user != null) {
            TextView logout = text("Çıkış", 12, ORANGE_DARK, Typeface.BOLD);
            logout.setPadding(dp(11), dp(7), dp(11), dp(7));
            logout.setBackground(rounded(ORANGE_SOFT, dp(999), ORANGE, dp(1)));
            logout.setOnClickListener(v -> {
                SessionManager.logout();
                renderLogin();
            });
            bar.addView(logout);
        }

        return bar;
    }

    private LinearLayout brandHeader() {
        LinearLayout hero = new LinearLayout(this);
        hero.setOrientation(LinearLayout.VERTICAL);
        hero.setPadding(dp(16), dp(16), dp(16), dp(16));
        hero.setBackground(gradient(ORANGE, ORANGE_DARK));

        LinearLayout top = new LinearLayout(this);
        top.setOrientation(LinearLayout.HORIZONTAL);
        top.setGravity(Gravity.CENTER_VERTICAL);

        TextView logo = text("Migros", 16, Color.WHITE, Typeface.BOLD);
        logo.setGravity(Gravity.CENTER);
        logo.setPadding(dp(12), dp(6), dp(12), dp(6));
        logo.setBackground(rounded(Color.argb(42, 255, 255, 255), dp(999), Color.argb(80, 255, 255, 255), dp(1)));
        top.addView(logo);

        TextView region = text("  Güney Marmara", 14, Color.WHITE, Typeface.BOLD);
        top.addView(region, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        hero.addView(top);
        addSpace(hero, 10);
        hero.addView(text("Kampanya Takip", 20, Color.WHITE, Typeface.BOLD));
        hero.addView(text("Şube kampanyalarını sade şekilde gör, tek onayla tamamla.", 13, Color.rgb(255, 244, 235), Typeface.NORMAL));
        return hero;
    }

    private void renderLogin() {
        currentScreen = "login";

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(16), dp(18), dp(16), dp(18));
        root.setBackgroundColor(BACKGROUND);

        scroll.addView(root, new ScrollView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        root.addView(brandHeader(), fullWidthBottom(14));

        LinearLayout storeCard = card();
        root.addView(storeCard);
        storeCard.addView(text("Mağaza girişi", 18, TEXT, Typeface.BOLD));
        storeCard.addView(text("Çalışanlar mağaza kodu ile giriş yapar. Kod mağaza adıyla eşleşir.", 13, MUTED, Typeface.NORMAL));
        addSpace(storeCard, 10);

        EditText storeCode = input("Mağaza kodu");
        storeCode.setInputType(InputType.TYPE_CLASS_NUMBER);
        storeCard.addView(storeCode);
        addSpace(storeCard, 10);

        Button storeLogin = button("Mağaza kodu ile giriş yap");
        storeCard.addView(storeLogin);
        storeLogin.setOnClickListener(v -> {
            AppUser user = SessionManager.loginWithStoreCode(storeCode.getText().toString());
            if (user == null) {
                Toast.makeText(this, "Mağaza kodu bulunamadı", Toast.LENGTH_SHORT).show();
            } else {
                renderHome();
            }
        });

        addSpace(storeCard, 8);
        TextView testCode = text("Test: 8876 → Aziziye Mahallesi MM Migros", 12, ORANGE_DARK, Typeface.BOLD);
        testCode.setPadding(dp(10), dp(8), dp(10), dp(8));
        testCode.setBackground(rounded(ORANGE_SOFT, dp(10), 0, 0));
        testCode.setOnClickListener(v -> storeCode.setText("8876"));
        storeCard.addView(testCode);

        LinearLayout adminCard = card();
        root.addView(adminCard);
        adminCard.addView(text("Admin girişi", 18, TEXT, Typeface.BOLD));
        adminCard.addView(text("Bölge yönetimi için e-posta ve şifre kullanılır.", 13, MUTED, Typeface.NORMAL));
        addSpace(adminCard, 10);

        EditText email = input("Admin e-posta");
        email.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        adminCard.addView(email);
        addSpace(adminCard, 8);

        EditText password = input("Şifre");
        password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        adminCard.addView(password);
        addSpace(adminCard, 10);

        Button adminLogin = outlineButton("Admin olarak giriş yap");
        adminCard.addView(adminLogin);
        adminLogin.setOnClickListener(v -> {
            AppUser user = SessionManager.loginAdmin(email.getText().toString(), password.getText().toString());
            if (user == null) {
                Toast.makeText(this, "Admin e-posta veya şifre hatalı", Toast.LENGTH_SHORT).show();
            } else {
                renderHome();
            }
        });

        addSpace(adminCard, 8);
        TextView testAdmin = text("Admin: admin@gm-kampanya.test / 123456", 12, MUTED, Typeface.NORMAL);
        testAdmin.setOnClickListener(v -> {
            email.setText("admin@gm-kampanya.test");
            password.setText("123456");
        });
        adminCard.addView(testAdmin);

        setScreen(scroll);
    }

    private EditText input(String hint) {
        EditText input = new EditText(this);
        input.setHint(hint);
        input.setSingleLine(true);
        input.setTextSize(14);
        input.setTextColor(TEXT);
        input.setHintTextColor(Color.rgb(148, 163, 184));
        input.setPadding(dp(12), 0, dp(12), 0);
        input.setBackground(rounded(Color.WHITE, dp(12), CARD_BORDER, dp(1)));
        input.setMinHeight(dp(46));
        input.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        return input;
    }

    private void renderHome() {
        currentScreen = "home";
        AppUser user = SessionManager.getCurrentUser();

        LinearLayout content = contentWithToolbar("Kampanya Takip", false);
        LinearLayout root = (LinearLayout) content.getTag();

        LinearLayout hero = card();
        hero.setBackground(gradient(ORANGE, ORANGE_DARK));
        content.addView(hero);
        hero.addView(text(user.role == UserRole.ADMIN ? "Bölge yönetimi" : user.storeName, 18, Color.WHITE, Typeface.BOLD));
        String subtitle = user.role == UserRole.ADMIN
                ? "Tüm şube kampanya durumlarını takip edebilirsin."
                : "Mağaza kodu: " + user.storeCode + " • Kampanyayı tek onayla tamamla.";
        hero.addView(text(subtitle, 12, Color.rgb(255, 244, 235), Typeface.NORMAL));

        LinearLayout statRow = new LinearLayout(this);
        statRow.setOrientation(LinearLayout.HORIZONTAL);
        content.addView(statRow, fullWidthBottom(10));
        addStat(statRow, "Aktif", String.valueOf(activeCampaignCount()), "kampanya");
        addStat(statRow, user.role == UserRole.ADMIN ? "Şube" : "Onay", user.role == UserRole.ADMIN ? String.valueOf(CampaignRepository.getTrackedStoreCount()) : employeeStatusSummary(user), user.role == UserRole.ADMIN ? "takipte" : "durum");

        LinearLayout actions = card();
        content.addView(actions);
        actions.addView(text("İşlemler", 16, TEXT, Typeface.BOLD));
        addSpace(actions, 8);

        Button campaigns = button("Kampanyaları görüntüle");
        actions.addView(campaigns);
        campaigns.setOnClickListener(v -> renderCampaigns());

        if (user.isManagerOrAdmin()) {
            addSpace(actions, 8);
            Button manager = outlineButton("Yönetici takip paneli");
            actions.addView(manager);
            manager.setOnClickListener(v -> renderManagerDashboard());

            addSpace(actions, 8);
            Button create = outlineButton("Yeni kampanya oluştur");
            actions.addView(create);
            create.setOnClickListener(v -> renderCreateCampaign());
        }

        content.addView(sectionTitle("Aktif kampanyalar"));
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
        if (user.role != UserRole.EMPLOYEE) {
            return "Panel";
        }

        int approved = 0;
        int total = 0;
        for (Campaign campaign : CampaignRepository.getCampaigns()) {
            CampaignProgress progress = CampaignRepository.getProgressForUser(campaign, user);
            total++;
            if (progress.status == CampaignProgressStatus.APPROVED) {
                approved++;
            }
        }
        return approved + "/" + total;
    }

    private void addStat(LinearLayout parent, String title, String value, String subtitle) {
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setPadding(dp(12), dp(12), dp(12), dp(12));
        box.setBackground(rounded(Color.WHITE, dp(14), CARD_BORDER, dp(1)));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        params.setMargins(0, 0, dp(8), 0);
        parent.addView(box, params);

        box.addView(text(title, 12, MUTED, Typeface.BOLD));
        box.addView(text(value, 20, ORANGE_DARK, Typeface.BOLD));
        box.addView(text(subtitle, 11, MUTED, Typeface.NORMAL));
    }

    private TextView sectionTitle(String value) {
        TextView title = text(value, 16, TEXT, Typeface.BOLD);
        title.setPadding(0, dp(4), 0, dp(8));
        return title;
    }

    private void renderCampaigns() {
        currentScreen = "campaigns";

        LinearLayout content = contentWithToolbar("Kampanyalar", true);
        LinearLayout root = (LinearLayout) content.getTag();

        content.addView(text("Kampanya listesi", 18, TEXT, Typeface.BOLD));
        content.addView(text("Detaya gir, açıklamayı kontrol et, mağazada aktifse tek onay ver.", 13, MUTED, Typeface.NORMAL));
        addSpace(content, 10);

        for (Campaign campaign : CampaignRepository.getCampaigns()) {
            content.addView(campaignCard(campaign));
        }

        setScreen(root);
    }

    private LinearLayout campaignCard(Campaign campaign) {
        LinearLayout card = card();

        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);

        TextView icon = text("M", 16, Color.WHITE, Typeface.BOLD);
        icon.setGravity(Gravity.CENTER);
        icon.setBackground(rounded(ORANGE, dp(12), 0, 0));
        header.addView(icon, new LinearLayout.LayoutParams(dp(42), dp(42)));

        LinearLayout titleBox = new LinearLayout(this);
        titleBox.setOrientation(LinearLayout.VERTICAL);
        titleBox.setPadding(dp(10), 0, 0, 0);
        titleBox.addView(text(campaign.title, 15, TEXT, Typeface.BOLD));
        titleBox.addView(text(campaign.startDate + " - " + campaign.endDate, 11, MUTED, Typeface.NORMAL));
        header.addView(titleBox, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        header.addView(statusChip(campaign.status.displayName(), campaign.status == com.guneymarmara.kampanyatakip.data.CampaignStatus.ACTIVE));
        card.addView(header);

        addSpace(card, 8);
        card.addView(text(campaign.description, 13, MUTED, Typeface.NORMAL));

        addSpace(card, 8);
        LinearLayout chipRow = new LinearLayout(this);
        chipRow.setOrientation(LinearLayout.HORIZONTAL);
        chipRow.addView(chip(campaign.priority, ORANGE_SOFT, ORANGE_DARK));
        addSpaceHorizontal(chipRow, 6);
        chipRow.addView(chip(campaign.targetGroup, Color.rgb(241, 245, 249), Color.rgb(71, 85, 105)));
        card.addView(chipRow);

        addSpace(card, 10);
        Button details = outlineButton("Aç");
        card.addView(details);
        details.setOnClickListener(v -> renderCampaignDetail(campaign));

        return card;
    }

    private TextView statusChip(String label, boolean active) {
        return active
                ? chip(label, Color.rgb(220, 252, 231), SUCCESS)
                : chip(label, Color.rgb(241, 245, 249), Color.rgb(71, 85, 105));
    }

    private void renderCampaignDetail(Campaign campaign) {
        currentScreen = "detail";
        openedCampaign = campaign;

        AppUser user = SessionManager.getCurrentUser();

        LinearLayout content = contentWithToolbar("Kampanya Detayı", true);
        LinearLayout root = (LinearLayout) content.getTag();

        LinearLayout hero = card();
        hero.setBackground(gradient(ORANGE, ORANGE_DARK));
        content.addView(hero);
        hero.addView(text(campaign.visualTitle, 18, Color.WHITE, Typeface.BOLD));
        hero.addView(text(campaign.discountText, 13, Color.rgb(255, 244, 235), Typeface.BOLD));

        LinearLayout detail = card();
        content.addView(detail);
        detail.addView(text(campaign.title, 18, TEXT, Typeface.BOLD));
        detail.addView(text(campaign.description, 13, MUTED, Typeface.NORMAL));
        addSpace(detail, 8);
        detail.addView(infoLine("Başlangıç", campaign.startDate));
        detail.addView(infoLine("Bitiş", campaign.endDate));
        detail.addView(infoLine("Hedef", campaign.targetGroup));

        LinearLayout taskInfo = card();
        content.addView(taskInfo);
        taskInfo.addView(text("Mağazada kontrol edilecekler", 16, TEXT, Typeface.BOLD));
        taskInfo.addView(text("Bunlar bilgilendirme amaçlıdır. Ayrı ayrı onay yoktur.", 12, MUTED, Typeface.NORMAL));
        addSpace(taskInfo, 8);
        for (CampaignTask task : campaign.tasks) {
            taskInfo.addView(text("• " + task.title, 13, TEXT, Typeface.BOLD));
            taskInfo.addView(text(task.description, 12, MUTED, Typeface.NORMAL));
            addSpace(taskInfo, 5);
        }

        if (user.role == UserRole.EMPLOYEE) {
            CampaignProgress progress = CampaignRepository.getProgressForUser(campaign, user);
            progress.markSeen();
            renderEmployeeSingleApproval(content, campaign, progress);
        } else {
            renderManagerCampaignInfo(content, campaign);
        }

        setScreen(root);
    }

    private TextView infoLine(String label, String value) {
        return text(label + ": " + value, 12, TEXT, Typeface.BOLD);
    }

    private void renderEmployeeSingleApproval(LinearLayout content, Campaign campaign, CampaignProgress progress) {
        LinearLayout approval = card();
        content.addView(approval);

        approval.addView(text("Mağaza durumu", 16, TEXT, Typeface.BOLD));
        approval.addView(text(progress.storeName, 13, MUTED, Typeface.NORMAL));
        addSpace(approval, 6);
        approval.addView(statusChip(progress.status));

        addSpace(approval, 10);
        if (progress.status == CampaignProgressStatus.APPROVED) {
            approval.addView(text("Bu kampanya mağazan için onaylandı.", 13, SUCCESS, Typeface.BOLD));
            addSpace(approval, 8);
            approval.addView(disabledButton("Onay verildi"));
        } else {
            approval.addView(text("Kampanya mağazada aktifse aşağıdaki tek butonla bildir.", 13, MUTED, Typeface.NORMAL));
            addSpace(approval, 8);
            Button approve = button("Kampanyayı mağazamda aktif olarak işaretle");
            approval.addView(approve);
            approve.setOnClickListener(v -> {
                progress.approve(campaign);
                Toast.makeText(this, "Kampanya mağazan için onaylandı", Toast.LENGTH_SHORT).show();
                renderCampaignDetail(campaign);
            });
        }
    }

    private void renderManagerCampaignInfo(LinearLayout content, Campaign campaign) {
        LinearLayout managerCard = card();
        content.addView(managerCard);

        managerCard.addView(text("Yönetici görünümü", 16, TEXT, Typeface.BOLD));
        managerCard.addView(text("Bu ekranda onay verilmez. Sadece kampanya bilgisi ve şube durumları takip edilir.", 13, MUTED, Typeface.NORMAL));
        addSpace(managerCard, 10);

        Button dashboard = button("Bu kampanyanın durumunu görüntüle");
        managerCard.addView(dashboard);
        dashboard.setOnClickListener(v -> renderManagerDashboard(campaign.id));
    }

    private void renderManagerDashboard() {
        renderManagerDashboard(-1L);
    }

    private void renderManagerDashboard(long onlyCampaignId) {
        currentScreen = "manager";
        AppUser user = SessionManager.getCurrentUser();

        if (user == null || !user.isManagerOrAdmin()) {
            Toast.makeText(this, "Bu ekran için admin yetkisi gerekir", Toast.LENGTH_SHORT).show();
            renderHome();
            return;
        }

        LinearLayout content = contentWithToolbar("Yönetici Paneli", true);
        LinearLayout root = (LinearLayout) content.getTag();

        content.addView(text("Şube onay takibi", 18, TEXT, Typeface.BOLD));
        content.addView(text("Yönetici burada sadece kimin yaptığını, hangi mağazanın beklediğini görür.", 13, MUTED, Typeface.NORMAL));
        addSpace(content, 10);

        int waiting = 0;
        int seen = 0;
        int approved = 0;

        for (CampaignProgress progress : CampaignRepository.getAllProgress()) {
            if (onlyCampaignId != -1L && progress.campaignId != onlyCampaignId) {
                continue;
            }
            if (progress.status == CampaignProgressStatus.APPROVED) {
                approved++;
            } else if (progress.status == CampaignProgressStatus.SEEN || progress.status == CampaignProgressStatus.IN_PROGRESS) {
                seen++;
            } else {
                waiting++;
            }
        }

        LinearLayout statRow = new LinearLayout(this);
        statRow.setOrientation(LinearLayout.HORIZONTAL);
        content.addView(statRow, fullWidthBottom(10));
        addMiniStat(statRow, "Bekliyor", waiting);
        addMiniStat(statRow, "Görüldü", seen);
        addMiniStat(statRow, "Yapıldı", approved);

        for (Campaign campaign : CampaignRepository.getCampaigns()) {
            if (onlyCampaignId != -1L && campaign.id != onlyCampaignId) {
                continue;
            }

            LinearLayout block = card();
            content.addView(block);

            block.addView(text(campaign.title, 16, TEXT, Typeface.BOLD));
            block.addView(text(campaign.startDate + " - " + campaign.endDate, 12, MUTED, Typeface.NORMAL));
            addSpace(block, 10);

            List<CampaignProgress> progressList = CampaignRepository.getProgressForCampaign(campaign.id);
            for (CampaignProgress progress : progressList) {
                block.addView(progressRow(progress));
            }
        }

        setScreen(root);
    }

    private void addMiniStat(LinearLayout parent, String title, int count) {
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setGravity(Gravity.CENTER);
        box.setPadding(dp(8), dp(10), dp(8), dp(10));
        box.setBackground(rounded(Color.WHITE, dp(14), CARD_BORDER, dp(1)));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        params.setMargins(0, 0, dp(8), 0);
        parent.addView(box, params);

        box.addView(text(String.valueOf(count), 19, ORANGE_DARK, Typeface.BOLD));
        box.addView(text(title, 11, MUTED, Typeface.BOLD));
    }

    private View progressRow(CampaignProgress progress) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.VERTICAL);
        row.setPadding(dp(10), dp(9), dp(10), dp(9));
        row.setBackground(rounded(Color.rgb(248, 250, 252), dp(12), CARD_BORDER, dp(1)));
        row.setLayoutParams(fullWidthBottom(8));

        LinearLayout top = new LinearLayout(this);
        top.setOrientation(LinearLayout.HORIZONTAL);
        top.setGravity(Gravity.CENTER_VERTICAL);

        TextView name = text(progress.userName, 14, TEXT, Typeface.BOLD);
        top.addView(name, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        top.addView(statusChip(progress.status));
        row.addView(top);

        row.addView(text(progress.storeName, 12, MUTED, Typeface.NORMAL));
        row.addView(text("Görüldü: " + emptyDash(progress.seenAt) + "  •  Onay: " + emptyDash(progress.completedAt), 11, MUTED, Typeface.NORMAL));

        return row;
    }

    private String emptyDash(String value) {
        return value == null || value.length() == 0 ? "-" : value;
    }

    private TextView statusChip(CampaignProgressStatus status) {
        switch (status) {
            case APPROVED:
                return chip("Yapıldı", Color.rgb(220, 252, 231), SUCCESS);
            case IN_PROGRESS:
            case SEEN:
                return chip("Görüldü", Color.rgb(255, 237, 213), WARNING);
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
            Toast.makeText(this, "Kampanya oluşturmak için admin yetkisi gerekir", Toast.LENGTH_SHORT).show();
            renderHome();
            return;
        }

        LinearLayout content = contentWithToolbar("Kampanya Oluştur", true);
        LinearLayout root = (LinearLayout) content.getTag();

        content.addView(text("Yeni kampanya girişi", 18, TEXT, Typeface.BOLD));
        content.addView(text("MVP sürüm. Görsel yükleme ve push bildirim sonraki adımda eklenecek.", 13, MUTED, Typeface.NORMAL));
        addSpace(content, 10);

        LinearLayout form = card();
        content.addView(form);

        EditText title = input("Kampanya adı");
        title.setText("Aziziye Fırsat Alanı Kontrolü");
        form.addView(title);
        addSpace(form, 8);

        EditText visual = input("Kapak başlığı");
        visual.setText("Migros Fırsatları");
        form.addView(visual);
        addSpace(form, 8);

        EditText description = input("Açıklama");
        description.setSingleLine(false);
        description.setMinLines(3);
        description.setText("Kampanya alanı, fiyat etiketi ve raf düzeni kontrol edilecek.");
        form.addView(description);
        addSpace(form, 8);

        EditText start = input("Başlangıç tarih/saat");
        start.setText("15.06.2026 09:00");
        form.addView(start);
        addSpace(form, 8);

        EditText end = input("Bitiş tarih/saat");
        end.setText("22.06.2026 22:00");
        form.addView(end);
        addSpace(form, 8);

        EditText discount = input("Kısa alt metin");
        discount.setText("Haftalık mağaza uygulama kontrolü");
        form.addView(discount);
        addSpace(form, 8);

        EditText target = input("Hedef şube/personel");
        target.setText("Güney Marmara şubeleri");
        form.addView(target);
        addSpace(form, 10);

        Button save = button("Kampanyayı kaydet");
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
