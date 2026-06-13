package com.guneymarmara.kampanyatakip;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.guneymarmara.kampanyatakip.data.AppUser;
import com.guneymarmara.kampanyatakip.data.Campaign;
import com.guneymarmara.kampanyatakip.data.CampaignProgress;
import com.guneymarmara.kampanyatakip.data.CampaignProgressStatus;
import com.guneymarmara.kampanyatakip.data.CampaignRepository;
import com.guneymarmara.kampanyatakip.data.CampaignStatus;
import com.guneymarmara.kampanyatakip.data.CampaignTask;
import com.guneymarmara.kampanyatakip.data.SessionManager;
import com.guneymarmara.kampanyatakip.data.Store;
import com.guneymarmara.kampanyatakip.data.StoreRepository;
import com.guneymarmara.kampanyatakip.data.UserRole;

import java.util.List;

public class MainActivity extends Activity {

    private static final int TEXT = Color.rgb(31, 41, 51);
    private static final int MUTED = Color.rgb(107, 114, 128);
    private static final int ORANGE_DARK = Color.rgb(217, 107, 18);
    private static final int SUCCESS = Color.rgb(22, 131, 74);
    private static final int WARNING = Color.rgb(180, 83, 9);

    private String currentScreen = "login";
    private Campaign openedCampaign;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(ORANGE_DARK);
        if (SessionManager.getCurrentUser() == null) {
            renderLogin();
        } else {
            renderHome();
        }
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }

    private View inflate(int layoutId, ViewGroup parent) {
        return LayoutInflater.from(this).inflate(layoutId, parent, false);
    }

    private void renderLogin() {
        currentScreen = "login";
        setContentView(R.layout.screen_login);

        EditText loginInput = findViewById(R.id.inputLogin);
        EditText passwordInput = findViewById(R.id.inputPassword);
        TextView error = findViewById(R.id.tvLoginError);
        TextView storeListPreview = findViewById(R.id.tvStoreListPreview);
        Button loginButton = findViewById(R.id.btnLogin);

        storeListPreview.setText(storePreviewText());

        loginButton.setOnClickListener(v -> {
            error.setVisibility(View.GONE);
            AppUser user = SessionManager.login(
                    this,
                    loginInput.getText().toString(),
                    passwordInput.getText().toString()
            );
            if (user == null) {
                error.setText("Mağaza kodu veya şifre hatalı.");
                error.setVisibility(View.VISIBLE);
                return;
            }
            renderHome();
        });
    }

    private String storePreviewText() {
        StringBuilder builder = new StringBuilder();
        for (Store store : StoreRepository.getStores()) {
            if (builder.length() > 0) builder.append("\n");
            builder.append(store.displayName());
        }
        return builder.toString();
    }

    private void renderHome() {
        currentScreen = "home";
        AppUser user = SessionManager.getCurrentUser();
        if (user == null) {
            renderLogin();
            return;
        }

        setContentView(R.layout.screen_home);
        fillToolbar(user);

        TextView storeTitle = findViewById(R.id.tvStoreTitle);
        TextView storeSubtitle = findViewById(R.id.tvStoreSubtitle);
        TextView roleChip = findViewById(R.id.tvRoleChip);
        TextView activeCount = findViewById(R.id.tvActiveCount);
        TextView approvalCount = findViewById(R.id.tvApprovalCount);
        Button campaignsButton = findViewById(R.id.btnCampaigns);
        Button managerButton = findViewById(R.id.btnManagerDashboard);
        Button createButton = findViewById(R.id.btnCreateCampaign);
        Button changePasswordButton = findViewById(R.id.btnChangePassword);
        LinearLayout campaignContainer = findViewById(R.id.campaignContainer);

        storeTitle.setText(user.storeDisplayName());
        storeSubtitle.setText(user.role == UserRole.ADMIN ? "Bölge yönetimi" : "Güney Marmara mağaza hesabı");
        roleChip.setText(user.role.displayName());
        activeCount.setText(String.valueOf(activeCampaignCount()));
        approvalCount.setText(user.role == UserRole.ADMIN ? managerApprovalSummary() : employeeApprovalSummary(user));

        campaignsButton.setOnClickListener(v -> renderCampaignList());
        managerButton.setVisibility(user.isManagerOrAdmin() ? View.VISIBLE : View.GONE);
        createButton.setVisibility(user.isManagerOrAdmin() ? View.VISIBLE : View.GONE);
        changePasswordButton.setVisibility(user.isStoreUser() ? View.VISIBLE : View.GONE);

        managerButton.setOnClickListener(v -> renderManagerDashboard(-1L));
        createButton.setOnClickListener(v -> renderCreateCampaign());
        changePasswordButton.setOnClickListener(v -> renderChangePassword());

        addCampaignCards(campaignContainer);
    }

    private void fillToolbar(AppUser user) {
        TextView toolbarTitle = findViewById(R.id.tvToolbarTitle);
        TextView toolbarSubtitle = findViewById(R.id.tvToolbarSubtitle);
        TextView logout = findViewById(R.id.btnLogout);
        if (toolbarTitle != null) toolbarTitle.setText("Kampanya Takip");
        if (toolbarSubtitle != null) toolbarSubtitle.setText(user.role == UserRole.ADMIN ? "Bölge Yönetimi" : user.storeDisplayName());
        if (logout != null) {
            logout.setOnClickListener(v -> {
                SessionManager.logout();
                renderLogin();
            });
        }
    }

    private int activeCampaignCount() {
        int count = 0;
        for (Campaign campaign : CampaignRepository.getCampaigns()) {
            if (campaign.status == CampaignStatus.ACTIVE || campaign.status == CampaignStatus.ENDING_SOON) {
                count++;
            }
        }
        return count;
    }

    private String employeeApprovalSummary(AppUser user) {
        int approved = 0;
        int total = 0;
        for (Campaign campaign : CampaignRepository.getCampaigns()) {
            CampaignProgress progress = CampaignRepository.getProgressForUser(campaign, user);
            if (progress == null) continue;
            total++;
            if (progress.status == CampaignProgressStatus.APPROVED) approved++;
        }
        return approved + "/" + total;
    }

    private String managerApprovalSummary() {
        int approved = 0;
        int total = 0;
        for (CampaignProgress progress : CampaignRepository.getAllProgress()) {
            total++;
            if (progress.status == CampaignProgressStatus.APPROVED) approved++;
        }
        return approved + "/" + total;
    }

    private void renderCampaignList() {
        currentScreen = "campaigns";
        setContentView(R.layout.screen_campaign_list);
        TextView back = findViewById(R.id.btnBack);
        LinearLayout campaignContainer = findViewById(R.id.campaignContainer);
        back.setOnClickListener(v -> renderHome());
        addCampaignCards(campaignContainer);
    }

    private void addCampaignCards(LinearLayout container) {
        container.removeAllViews();
        for (Campaign campaign : CampaignRepository.getCampaigns()) {
            View card = inflate(R.layout.item_campaign_card, container);
            TextView status = card.findViewById(R.id.tvStatus);
            TextView priority = card.findViewById(R.id.tvPriority);
            TextView title = card.findViewById(R.id.tvTitle);
            TextView description = card.findViewById(R.id.tvDescription);
            TextView date = card.findViewById(R.id.tvDate);

            status.setText(campaign.status.displayName());
            priority.setText(campaign.priority);
            title.setText(campaign.title);
            description.setText(campaign.description);
            date.setText(campaign.startDate + "  →  " + campaign.endDate);
            card.setOnClickListener(v -> renderCampaignDetail(campaign));
            container.addView(card);
        }
    }

    private void renderCampaignDetail(Campaign campaign) {
        currentScreen = "detail";
        openedCampaign = campaign;
        AppUser user = SessionManager.getCurrentUser();
        if (user == null) {
            renderLogin();
            return;
        }

        setContentView(R.layout.screen_campaign_detail);
        TextView back = findViewById(R.id.btnBack);
        TextView status = findViewById(R.id.tvCampaignStatus);
        TextView title = findViewById(R.id.tvCampaignTitle);
        TextView description = findViewById(R.id.tvCampaignDescription);
        TextView date = findViewById(R.id.tvCampaignDate);
        LinearLayout taskContainer = findViewById(R.id.taskContainer);
        LinearLayout employeePanel = findViewById(R.id.employeePanel);
        TextView myProgress = findViewById(R.id.tvMyProgress);
        Button approveButton = findViewById(R.id.btnApproveCampaign);

        back.setOnClickListener(v -> renderCampaignList());
        status.setText(campaign.status.displayName());
        title.setText(campaign.title);
        description.setText(campaign.description);
        date.setText(campaign.startDate + "  →  " + campaign.endDate + "\nHedef: " + campaign.targetGroup);

        taskContainer.removeAllViews();
        for (CampaignTask task : campaign.tasks) {
            View taskView = inflate(R.layout.item_task_info, taskContainer);
            TextView taskTitle = taskView.findViewById(R.id.tvTaskTitle);
            TextView taskDescription = taskView.findViewById(R.id.tvTaskDescription);
            taskTitle.setText(task.title);
            taskDescription.setText(task.description);
            taskContainer.addView(taskView);
        }

        if (!user.isStoreUser()) {
            employeePanel.setVisibility(View.GONE);
            return;
        }

        CampaignProgress progress = CampaignRepository.getProgressForUser(campaign, user);
        if (progress == null) {
            employeePanel.setVisibility(View.GONE);
            return;
        }
        progress.markSeen();
        myProgress.setText("Mağaza durumu: " + progress.status.displayName());

        if (progress.status == CampaignProgressStatus.APPROVED) {
            approveButton.setText("Kampanya Tamamlandı");
            approveButton.setEnabled(false);
            approveButton.setBackgroundResource(R.drawable.bg_soft_card);
            approveButton.setTextColor(SUCCESS);
        } else {
            approveButton.setText("Kampanyayı Mağazamda Aktif Yaptım");
            approveButton.setEnabled(true);
            approveButton.setOnClickListener(v -> {
                progress.approve();
                Toast.makeText(this, "Kampanya mağaza için tamamlandı", Toast.LENGTH_SHORT).show();
                renderCampaignDetail(campaign);
            });
        }
    }

    private void renderManagerDashboard(long onlyCampaignId) {
        currentScreen = "manager";
        AppUser user = SessionManager.getCurrentUser();
        if (user == null || !user.isManagerOrAdmin()) {
            Toast.makeText(this, "Bu ekran için yönetici yetkisi gerekir", Toast.LENGTH_SHORT).show();
            renderHome();
            return;
        }

        setContentView(R.layout.screen_manager_dashboard);
        TextView back = findViewById(R.id.btnBack);
        TextView waitingCount = findViewById(R.id.tvWaitingCount);
        TextView approvedCount = findViewById(R.id.tvApprovedCount);
        LinearLayout progressContainer = findViewById(R.id.progressContainer);

        back.setOnClickListener(v -> renderHome());

        int waiting = 0;
        int approved = 0;
        for (CampaignProgress progress : CampaignRepository.getAllProgress()) {
            if (onlyCampaignId != -1L && progress.campaignId != onlyCampaignId) continue;
            if (progress.status == CampaignProgressStatus.APPROVED) approved++;
            else waiting++;
        }
        waitingCount.setText("Bekleyen\n" + waiting);
        approvedCount.setText("Tamamlanan\n" + approved);

        progressContainer.removeAllViews();
        for (Campaign campaign : CampaignRepository.getCampaigns()) {
            if (onlyCampaignId != -1L && campaign.id != onlyCampaignId) continue;
            addManagerCampaignBlock(progressContainer, campaign);
        }
    }

    private void addManagerCampaignBlock(LinearLayout container, Campaign campaign) {
        TextView header = new TextView(this);
        header.setText(campaign.title + "\n" + campaign.startDate + " → " + campaign.endDate);
        header.setTextColor(TEXT);
        header.setTextSize(15);
        header.setTypeface(null, android.graphics.Typeface.BOLD);
        header.setPadding(0, dp(10), 0, dp(8));
        container.addView(header);

        List<CampaignProgress> progressList = CampaignRepository.getProgressForCampaign(campaign.id);
        for (CampaignProgress progress : progressList) {
            View row = inflate(R.layout.item_progress_row, container);
            TextView store = row.findViewById(R.id.tvStore);
            TextView progressStatus = row.findViewById(R.id.tvProgressStatus);
            TextView meta = row.findViewById(R.id.tvProgressMeta);

            store.setText(progress.storeCode + " • " + progress.storeName);
            progressStatus.setText(progress.status.displayName());
            progressStatus.setTextColor(progress.status == CampaignProgressStatus.APPROVED ? SUCCESS : WARNING);
            progressStatus.setBackgroundResource(progress.status == CampaignProgressStatus.APPROVED
                    ? R.drawable.bg_chip_success
                    : R.drawable.bg_chip_orange);
            meta.setText("Görüldü: " + emptyDash(progress.seenAt) + "  •  Tamamlandı: " + emptyDash(progress.completedAt));
            container.addView(row);
        }
    }

    private String emptyDash(String value) {
        return value == null || value.trim().isEmpty() ? "-" : value;
    }

    private void renderChangePassword() {
        currentScreen = "password";
        AppUser user = SessionManager.getCurrentUser();
        if (user == null || !user.isStoreUser()) {
            Toast.makeText(this, "Şifre değiştirme mağaza hesabı için kullanılır", Toast.LENGTH_SHORT).show();
            renderHome();
            return;
        }

        setContentView(R.layout.screen_change_password);
        TextView back = findViewById(R.id.btnBack);
        TextView storeName = findViewById(R.id.tvStoreName);
        EditText oldPassword = findViewById(R.id.inputOldPassword);
        EditText newPassword = findViewById(R.id.inputNewPassword);
        EditText confirmPassword = findViewById(R.id.inputConfirmPassword);
        TextView error = findViewById(R.id.tvPasswordError);
        Button save = findViewById(R.id.btnSavePassword);

        storeName.setText(user.storeDisplayName());
        back.setOnClickListener(v -> renderHome());
        save.setOnClickListener(v -> {
            error.setVisibility(View.GONE);
            String next = newPassword.getText().toString().trim();
            String confirm = confirmPassword.getText().toString().trim();
            if (next.length() < 6) {
                showPasswordError(error, "Yeni şifre en az 6 karakter olmalı.");
                return;
            }
            if (!next.equals(confirm)) {
                showPasswordError(error, "Yeni şifreler eşleşmiyor.");
                return;
            }
            boolean changed = SessionManager.changeCurrentStorePassword(
                    this,
                    oldPassword.getText().toString(),
                    next
            );
            if (!changed) {
                showPasswordError(error, "Mevcut şifre hatalı.");
                return;
            }
            Toast.makeText(this, "Şifre değiştirildi", Toast.LENGTH_SHORT).show();
            renderHome();
        });
    }

    private void showPasswordError(TextView error, String message) {
        error.setText(message);
        error.setVisibility(View.VISIBLE);
    }

    private void renderCreateCampaign() {
        currentScreen = "create";
        AppUser user = SessionManager.getCurrentUser();
        if (user == null || !user.isManagerOrAdmin()) {
            Toast.makeText(this, "Kampanya oluşturmak için yönetici yetkisi gerekir", Toast.LENGTH_SHORT).show();
            renderHome();
            return;
        }

        setContentView(R.layout.screen_create_campaign);
        TextView back = findViewById(R.id.btnBack);
        EditText title = findViewById(R.id.inputTitle);
        EditText description = findViewById(R.id.inputDescription);
        EditText start = findViewById(R.id.inputStart);
        EditText end = findViewById(R.id.inputEnd);
        EditText target = findViewById(R.id.inputTarget);
        Button save = findViewById(R.id.btnSaveCampaign);

        back.setOnClickListener(v -> renderHome());
        title.setText("Yeni Kampanya Uygulaması");
        description.setText("Mağaza kampanya alanı, fiyat etiketi ve raf düzeni kontrol edilecek.");
        start.setText("15.06.2026 09:00");
        end.setText("22.06.2026 22:00");
        target.setText("Güney Marmara mağazaları");

        save.setOnClickListener(v -> {
            String titleValue = title.getText().toString().trim();
            if (titleValue.isEmpty()) {
                Toast.makeText(this, "Kampanya adı zorunlu", Toast.LENGTH_SHORT).show();
                return;
            }
            Campaign created = CampaignRepository.addCampaign(
                    titleValue,
                    description.getText().toString().trim(),
                    start.getText().toString().trim(),
                    end.getText().toString().trim(),
                    target.getText().toString().trim()
            );
            Toast.makeText(this, "Kampanya oluşturuldu", Toast.LENGTH_SHORT).show();
            renderCampaignDetail(created);
        });
    }

    private void goBack() {
        if ("detail".equals(currentScreen)) {
            renderCampaignList();
        } else if ("campaigns".equals(currentScreen)
                || "manager".equals(currentScreen)
                || "create".equals(currentScreen)
                || "password".equals(currentScreen)) {
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
}
