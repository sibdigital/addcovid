package ru.sibdigital.addcovid.dto.esia;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserOrganization {

    private Long oid; // ИД организации
    private Long prnOid; // ИД пользователя
    private String fullName; // полное наименование организации
    private String shortName; // краткое наименование организации
    private String ogrn; // ОГРН организации
    private String type; // тип организации ("BUSINESS" - ИП, "LEGAL" - ЮЛ, "AGENCY" - ОГВ)
    private Boolean chief; // признак руководителя
    private Boolean admin; // признак наличия сотрудника в группе доступа "Администраторы профиля организации"
    private String phone;
    private String email; // служебная электронная почта
    private Boolean active; // признак блокировки сотрудника
    private Boolean hasRightOfSubstitution;
    private Boolean hasApprovalTabAccess;
    private Boolean isLiquidated;

    private String branchName; // наименование филиала
    private Long branchOid; // ИД филиала

    public boolean isChief() {
        return this.chief != null && this.chief.booleanValue();
    }

    public boolean isAdmin() {
        return this.admin != null && this.admin.booleanValue();
    }

    public boolean isActive() {
        return this.active != null && this.active.booleanValue();
    }

    public boolean isLiquidated() {
        return this.isLiquidated != null && this.isLiquidated.booleanValue();
    }
}
