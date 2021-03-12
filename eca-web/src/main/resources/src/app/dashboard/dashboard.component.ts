import { Component, OnInit } from '@angular/core';
import { MenuItem, MessageService } from 'primeng/api';
import { LogoutService } from "../auth/services/logout.service";
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

  private user: UserDto;

  public items: MenuItem[] = [];

  public userMenuItems: MenuItem[] = [];

  constructor(private logoutService: LogoutService,
              private usersService: UsersService,
              private messageService: MessageService) {
  }

  public ngOnInit() {
    this.getCurrentUser();
    this.initUserMenu();
  }

  public getUserLogin(): string {
    return this.user && this.user.login;
  }

  public isSuperAdmin(): boolean {
    return this.user && this.user.roles && this.user.roles.map((role: RoleDto) => role.roleName).includes(Role.ROLE_SUPER_ADMIN);
  }

  public getCurrentUser(): void {
    this.usersService.getCurrentUser().subscribe({
      next: (user: UserDto) => {
        this.user = user;
        this.initBaseMenu();
      },
      error: (error) => {
        this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
      }
    })
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
