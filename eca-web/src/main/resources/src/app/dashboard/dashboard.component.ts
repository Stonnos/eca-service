import { Component, OnInit } from '@angular/core';
import { MenuItem, MessageService } from 'primeng/api';
import { LogoutService } from "../auth/services/logout.service";
import { UserStorage } from "../auth/services/user.storage";
import { RoleDto, UserDto } from "../../../../../../target/generated-sources/typescript/eca-web-dto";
import { Role } from "../common/model/roles";
import { HttpErrorResponse } from "@angular/common/http";
import { UsersService } from "../users/services/users.service";

@Component({
  selector: 'app-dashboard',
  providers: [ LogoutService ],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {

  public items: MenuItem[];

  public userMenuItems: MenuItem[];

  constructor(private logoutService: LogoutService,
              private usersService: UsersService,
              private userStorage: UserStorage,
              private messageService: MessageService) {
  }

  public ngOnInit() {
    this.initBaseMenu();
    this.initUserMenu();
  }

  public getUserLogin(): string {
    const user: UserDto = this.userStorage.getUser();
    return user && user.login;
  }

  public isSuperAdmin(): boolean {
    const user: UserDto = this.userStorage.getUser();
    return user && user.roles && user.roles.map((role: RoleDto) => role.roleName).includes(Role.ROLE_SUPER_ADMIN);
  }

  public logout() {
    this.usersService.logoutRequest()
      .subscribe({
        next: () => {
          this.logoutService.logout();
        },
        error: (error) => {
          this.handleLogoutError(error);
        }
      });
  }

  private handleLogoutError(error): void {
    if (error instanceof HttpErrorResponse) {
      if (error.status === 401) {
        this.logoutService.logout();
      } else {
        this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
      }
    } else {
      this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
    }
  }

  private initBaseMenu(): void {
    this.items = [
      {
        label: 'Эксперименты',
        routerLink: ['/dashboard/experiments']
      },
      {
        label: 'Классификаторы',
        routerLink: ['/dashboard/classifiers']
      },
      {
        label: 'Оптимальные настройки классификаторов',
        routerLink: ['/dashboard/classifiers-options-requests']
      },
      {
        label: 'Датасеты',
        routerLink: ['/dashboard/instances']
      },
      {
        label: 'Пользователи',
        routerLink: ['/dashboard/users'],
        visible: this.isSuperAdmin()
      }
    ];
  }

  private initUserMenu(): void {
    this.userMenuItems = [
      {
        label: 'Настройки',
        icon: 'pi pi-cog',
        routerLink: ['/dashboard/profile']
      },
      {
        label: 'Выход',
        icon: 'pi pi-sign-out',
        command: () => {
          this.logout();
        }
      }
    ];
  }
}
