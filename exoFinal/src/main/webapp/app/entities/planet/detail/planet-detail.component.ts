import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IPlanet } from '../planet.model';

@Component({
  selector: 'jhi-planet-detail',
  templateUrl: './planet-detail.component.html',
})
export class PlanetDetailComponent implements OnInit {
  planet: IPlanet | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ planet }) => {
      this.planet = planet;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
