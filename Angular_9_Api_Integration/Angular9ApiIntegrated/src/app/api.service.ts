import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, throwError, Subject } from 'rxjs';
import { catchError, retry, map } from 'rxjs/operators';
import { StatsData } from './stats-data';
import { IBusy } from './app';

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  protected apiURL: string = 'http://3.1.217.199:8088';
  private readonly REQUEST_HEADERS = new HttpHeaders({ 'Content-Type': 'application/json; charset=utf-8' });
  private readonly REQUEST_OPTIONS = { headers: this.REQUEST_HEADERS };
  private busy: Subject<IBusy>;

  public response: StatsData[];

  constructor(private http: HttpClient) { }

  public setBusy(busy: Promise<any> | Observable<any>, message?: string, fullScreen: boolean = true): void {
    const busyState: IBusy = {
      busy: busy,
      message: (message ? message : 'Please wait...'),
      fullScreen: fullScreen
    };
    this.busy.next(busyState);

  }

  public getSocialDistancingDataBasedOnLatLong(lat, long) {
    var url = "/socialDistancingDataBasedOnLatLong?inputLat="+lat+"&inputLong="+long;
    return this.http.get<any>(`${this.apiURL}${url}`, this.REQUEST_OPTIONS);
  }

  public getDistanceToAvgViolationsWithLocationForGraph(lat, long) {
    var url = "/distanceToAvgViolationsWithLocationForGraph?inputLat="+lat+"&inputLong="+long;
    return this.http.get<any>(`${this.apiURL}${url}`, this.REQUEST_OPTIONS).pipe(map((data) => {
    let values = {};
    data.forEach(loc => {
      this.getLocationVsDistanceGraphAggregation(loc.distanceFromInputLocation, loc.contToAvgViolationsDayWise[loc.locality], loc.locality, values);
    });
    let resultArray = []
    for(let key in values) {
    if(Number.isInteger(+key)){
      //resultArray.push([key, +values[key].toFixed(2)]);
      var viol = +values[key].toFixed(2);
      var locality = values["locality"+key] + "- "+ key +" KM";
     var entry = {
          'name': [locality],
          'data': [viol]
        }
        resultArray.push(entry);
    }
    }
    return resultArray;
    }));

  }

  private getLocationVsDistanceGraphAggregation(distance, number, locality, value){ 
    const limit = parseInt(distance.toFixed(0));

    switch (limit) {
      case 0:
        value[limit +1] = value[limit + 1] ? value[limit + 1] + number : number;
        this.setLocalities(limit, value, locality);
        break;
      case 1:
        value[limit +1] = value[limit + 1] ? value[limit + 1] + number : number;
        this.setLocalities(limit, value, locality);
        break;
      case 2:
        value[limit +1] = value[limit + 1] ? value[limit + 1] + number : number;
        this.setLocalities(limit, value, locality);
        break;
      case 3:
        value[limit +1] = value[limit + 1] ? value[limit + 1] + number : number;
        this.setLocalities(limit, value, locality);
        break;
      case 4:
        value[limit +1] = value[limit + 1] ? value[limit + 1] + number : number;
        this.setLocalities(limit, value, locality);
        break;
      case 5:
        value[limit +1] = value[limit + 1] ? value[limit + 1] + number : number;
        this.setLocalities(limit, value, locality);
        break;
      case 6:
        value[limit +1] = value[limit + 1] ? value[limit + 1] + number : number;
        this.setLocalities(limit, value, locality);
        break;
      case 7:
        value[limit +1] = value[limit + 1] ? value[limit + 1] + number : number;
        this.setLocalities(limit, value, locality);
        break;
      case 8:
        value[limit +1] = value[limit + 1] ? value[limit + 1] + number : number;
        this.setLocalities(limit, value, locality);
        break;
      case 9:
        value[limit +1] = value[limit + 1] ? value[limit + 1] + number : number;
        this.setLocalities(limit, value, locality);
        break;
      default:
        value['10'] = value['10'] ? value['10'] + number : number;
        this.setLocalities(9, value, locality);
    }
  }

  private setLocalities(limit, value, locality){
        var no = limit+1;
        var localities = value['locality'+no];
        if(localities === '' || localities === undefined){
          localities = locality;
        }else{
          localities = localities + ", "+ locality;
        }
        value['locality'+no]=localities;  
  }

  public getPatternOfViolationAccrossDaysForGraph(lat, long) {
    var url = "/patternOfViolationAccrossDaysForGraph?inputLat="+lat+"&inputLong="+long;
    return this.http.get<any>(`${this.apiURL}${url}`, this.REQUEST_OPTIONS).pipe(map((data) => {
        return {
          'locality': data.locality,
          'dates': Object.keys(data.contToAvgViolationsDayWise).map(val=>val.substring(0, 6)),
          'violations': Object.values(data.contToAvgViolationsDayWise)
        }


    }));
  }

  public getAreaWiseZonePercentageForPieChart() {
    return this.http.get<StatsData[]>(`${this.apiURL}/areaWiseZonePercentageForPieChart?inputLat=28.402&inputLong=77.498`, this.REQUEST_OPTIONS);
  }

  public getAllLocalities() {
    return this.http.get<StatsData[]>(`${this.apiURL}/allLocalities`, this.REQUEST_OPTIONS);
  }

  public getLocalitiesMappedLatLong() {
    return this.http.get<StatsData[]>(`${this.apiURL}/localitiesMappedLatLong`, this.REQUEST_OPTIONS);
  }

  public downloadVideo(lat, long) {
    var url = "/download?inputLat="+lat+"&inputLong="+long;
    return this.http.get<any>(`${this.apiURL}${url}`, this.REQUEST_OPTIONS);
  }

  public getCovidHighlight() {
    return this.http.get(`https://api.covid19api.com/summary`, this.REQUEST_OPTIONS);
  }

  
}
